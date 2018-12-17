package com.mx.santander.commons.interceptor;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import com.mx.santander.commons.exceptions.PropagationMessageException;
import com.mx.santander.commons.model.dto.ResponseTOWrapper;
import com.mx.santander.commons.utils.JacksonUtils;
import com.mx.santander.commons.utils.RequestUtils;

/**
 * 
 * Http Client Interceptor es el principal encargado de capturar todas la
 * peticiones y respuestas a otros servicios, valida si la respuesta es de tipo
 * application/json valida si la respuesta cumple con la siguiente estructura
 * 
 * <pre>
 * {
 * "codigoDeOperacion": 0, "mensaje":{ "titulo":"Atencion", "texto" :"Operacion
 * Exitosa", "codigoError":"ERR999", }, "data":{} }
 * </pre>
 * 
 * si cumple con la estructura valida si el codigo de operacion es diferente de
 * 0 significa que ocurrrio un problema en el microservicio invocado con lo cual
 * se lanza {@link PropagationMessageException} para que el codigo de error y
 * codigoDeOperacion sea propagado al siguiente microservicio
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public class CustomClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {
    /**
     * Variable de log general
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomClientHttpRequestInterceptor.class);
    private static final String XCHANNEL = "x-channel";

    /**
     * Metodo encargado de interceptar la peticion a cualquier otro servicio HTTP
     * 
     * @throws IOException
     *             error de acceso
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String urlService = request.getURI().toURL().toString();
        /**
         * siempre se propaga como minimo el canal del que proviene la peticion
         */
        HttpServletRequest originalRequest = RequestUtils.getCurrentHttpRequest();
        if (Objects.nonNull(originalRequest) && !request.getHeaders().containsKey(XCHANNEL)) {
            request.getHeaders().add(XCHANNEL, originalRequest.getHeader(XCHANNEL));
        }

        if (LOGGER.isTraceEnabled()) {
            StringBuilder log = new StringBuilder();
            log.append("\n\n*****************HTTP REQUEST***********************************************");
            log.append("\nRequest Path:" + urlService);
            log.append("\nRequest Method:" + request.getMethod().name());
            log.append("\nRequest Headers:\n" + JacksonUtils.writeValueAsStringPretty(request.getHeaders()));
            if (body.length > 0) {
                log.append("\n\nRequest Body:" + new String(body, "UTF-8"));
            }
            log.append("\n*******************************************************************************\n\n");
            LOGGER.trace(log.toString());
        }
        long timeStart = System.currentTimeMillis();
        ClientHttpResponse response = execution.execute(request, body);
        validateResponse(response, urlService);
        // se toma el tiempo una vez ya pudimos obtener la respuesta del servicio
        long timeEnd = System.currentTimeMillis();
        LOGGER.info("\nEl servicio:{} \nTermino en:{}ms\n\n", urlService, timeEnd - timeStart);
        return response;
    }

    /**
     * Metodo encargado de validar si la respuesta es un application/json y ver si
     * cumple con la estructura *
     * 
     * <pre>
    * {
    * "codigoDeOperacion": 0, "mensaje":{ "titulo":"Atencion", "texto" :"Operacion
    * Exitosa", "codigoError":"ERR999", }, "data":{} }
     * </pre>
     * 
     * @param response
     *            response del servicio
     * @param urlService
     *            url del servicio
     * @throws IOException
     *             exception de acceso
     */
    private void validateResponse(ClientHttpResponse response, String urlService) throws IOException {
        String responseBody = StreamUtils.copyToString(response.getBody(), Charset.defaultCharset());
        if (LOGGER.isTraceEnabled()) {
            StringBuilder log = new StringBuilder();
            log.append("\n\n*****************HTTP RESPONSE***********************************************");
            log.append("\nRequest Path:" + urlService);
            log.append("\nStatus Code:" + response.getStatusCode());
            log.append("\nResponse Headers:\n" + JacksonUtils.writeValueAsStringPretty(response.getHeaders()));
            log.append("\n\nResponse Body:" + responseBody);
            log.append("\n********************************************************************************\n\n");
            LOGGER.trace(log.toString());
        }
        MediaType mediaType = response.getHeaders().getContentType();
        // se valida que la respuesta sea un JSON
        if (!MediaType.APPLICATION_JSON.equals(mediaType) && !MediaType.APPLICATION_JSON_UTF8.equals(mediaType)) {
            LOGGER.debug("Se omite la validacion de ResponseTO ya que el ContentType no corresponde a [{} o {}] ",
                    MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON_UTF8);
            return;
        }

        // validamos el json de respuesta
        validateJsonResponse(responseBody, urlService);

    }

    /**
     * Metodo que se encarga de validar si la respuesta del microservicios fue
     * exitosa, si lo fue el flujo continua sin problemas, de los contrario se lanza
     * {@link PropagationMessageException} para que el error se propague entre las
     * llamadas de los microservicios
     * 
     * @param responseBody
     *            string de respuesta del servicio
     * @param urlService
     *            url del servicio
     */
    private void validateJsonResponse(String jsonBody, String urlService) {
        ResponseTOWrapper intentResponseTO = null;

        intentResponseTO = JacksonUtils.readValue(jsonBody, ResponseTOWrapper.class);

        // si el codigo de operacion es MIN_VALUE significa que el binding no
        // corresponde al formato de un ResponseTO
        if (Objects.isNull(intentResponseTO) || Objects.isNull(intentResponseTO.getCodigoDeOperacion())) {
            LOGGER.debug("La respuesta no corresponde al formato de un ResponseTO");
            return;
        }

        // si el codigo de operacion es 0 hay una respuesta exitosa del servicio
        if (intentResponseTO.getCodigoDeOperacion() == 0) {
            LOGGER.info("ejecucion del servicio:[{}] con EXITO", urlService);
            return;
        }

        // si el codigo de operacion es diferente de 0 entonces ocurrio un error o
        // advertencia
        if (0 != intentResponseTO.getCodigoDeOperacion()) {
            if (intentResponseTO.getCodigoDeOperacion() < 0) {
                LOGGER.error("Ejecucion del servicio:[{}] con ERROR CodigoDeOperacion:{} mensaje:{}", urlService,
                        intentResponseTO.getCodigoDeOperacion(), intentResponseTO.getMensaje().getTexto());
                throw new PropagationMessageException(intentResponseTO.getCodigoDeOperacion(),
                        intentResponseTO.getMensaje().getCodigoError(),
                        intentResponseTO.getMensaje().getTexto(),
                        "Ocurrio un error en el servicio:[" + urlService + "]");
            }
            // valida si el servicio se ejecuto correctamente pero con advertencia
            if (intentResponseTO.getCodigoDeOperacion() > 0) {
                LOGGER.warn("Ejecucion del servicio:[{}] con WARNING CodigoDeOperacion:{} , mensaje:{}", urlService,
                        intentResponseTO.getCodigoDeOperacion(), intentResponseTO.getMensaje().getTexto());
            }

        }
        return;
    }

}
