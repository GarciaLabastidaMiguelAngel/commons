package com.mx.santander.commons.interceptor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestTemplate;

import com.mx.santander.commons.utils.RequestUtils;

import lombok.Data;

/**
 * Clase que se encarga de la propagacion de los headers entre microservicios la
 * opcion siempre esta habilitada a menos que se establezca la propieedad
 * com.mx.santander.headers.propagation.enable en false
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Data
public class CustomHttpHeadersPropagationInterceptor implements ClientHttpRequestInterceptor {
    /**
     * logger de la clase
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomHttpHeadersPropagationInterceptor.class);
    /**
     * headers a ignorar en la propagacion
     */
    private List<String> ignorePropagationHeaders = new ArrayList<>();
    /**
     * trace de headers debug
     */
    private boolean traceHeadersDebug;

    /**
     * Metodo por el que pasan todos las peticiones echas con {@link RestTemplate} y
     * propaga los headers de la peticion que llego al servicio, a la nueva peticion
     */
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        LOGGER.debug("Comienza el interceptor para la propagacion de headers");
        // se accede al request original para extraer el header y propagar
        HttpServletRequest originalRequest = RequestUtils.getCurrentHttpRequest();
        /**
         * transferimos los headers de la peticion original al nuevo request para su
         * propagacion, omitiendo los headers de la lista ignorePropagationHeaders
         */
        Enumeration<String> hNames = null;
        if(Objects.nonNull(originalRequest)) {
            hNames=originalRequest.getHeaderNames();
        }
        while (Objects.nonNull(originalRequest) && hNames.hasMoreElements()) {
            String keyH = hNames.nextElement();
            // se validan los headers a ignorar en la propagacion
            if (ignorePropagationHeaders.contains(keyH)) {
                traceHeaders(
                        "[SKIP] Se omite la propagacion del header >>>> {} >>>> del request original de la peticion",
                        keyH);
                // coniuamos con el siguiente header
                continue;
            }
            // solo se tranfieren los headers que no existen en el request actual
            if (!request.getHeaders().containsKey(keyH) && !request.getHeaders().containsKey(keyH.toLowerCase())) {
                traceHeaders("[ADD] Se agrega el header >>>> {} >>>> para propagacion", keyH);
                // se agrega el header de la peticion original al nuevo request
                request.getHeaders().add(keyH, originalRequest.getHeader(keyH));
            }
        }
        // si la peticion es de tipo GET se omite el content type
        if (request.getMethod().equals(HttpMethod.GET)) {
            traceHeaders(
                    "[DELETE] La peticion es de tipo GET por lo cual se elimina el header >>>> content-type >>>> de la peticion");
            // se eliminan todas la convinaciones posibles que puede tener el header de
            // Content-Type
            request.getHeaders().remove("content-type");
            request.getHeaders().remove("Content-Type");
            request.getHeaders().remove("ContentType");
            request.getHeaders().remove("contenttype");
        }
        // se valida si el request contienen un header de la lista
        // ignorePropagationHeaders y se elimina de la peticion
        ignorePropagationHeaders.forEach(header -> {
            if (request.getHeaders().containsKey(header) || request.getHeaders().containsKey(header.toLowerCase())
                    || request.getHeaders().containsKey(header.toUpperCase())) {
                traceHeaders(
                        "[DELETE] Se elimina el header >>>> {} >>>> de la peticion, ya que no esta permitida su propagacion",
                        header);
                // se elimina el header de la peticion, y se eliminan todas las conbinaciones
                // posibles en que se puede resentar el header
                request.getHeaders().remove(header);
                request.getHeaders().remove(header.toLowerCase());
                request.getHeaders().remove(header.toUpperCase());
            }
        });
        return execution.execute(request, body);
    }

    /**
     * traza cuando se agregan o eliminan headers
     * 
     * @param msg
     *            logs msg
     */
    private void traceHeaders(String msg, Object... arg) {
        if (traceHeadersDebug) {
            LOGGER.debug(msg, arg);
        }
    }
}
