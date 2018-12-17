package com.mx.santander.commons.dto.aspect;

import static java.util.Objects.isNull;

import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mx.santander.commons.channel.security.aspec.ChannelAccessAspect;
import com.mx.santander.commons.model.dto.RequestTO;
import com.mx.santander.commons.model.dto.ResponseTO;
import com.mx.santander.commons.model.dto.ResponseTOWrapper;
import com.mx.santander.commons.model.dto.ResponseTOWrapper.ResponseMessageTO;

/**
 * Aspecto que camptura las peticiones al microservicio y da formato a las
 * respuesta de los servicios que se encuentren dentro de los paquetes
 * especificados por {@link #packageScan}, de lo contrario no da formato a las
 * respuestas, se establece con orden 4 ya que es necesario que se ejecunte
 * antes {@link ChannelAccessAspect} para validar que x-channel venga informado
 * ya que es requisito de seguridad saber de que canal proviene la peticion
 * 
 * la respuesta generaca establecida para GoPay es
 * 
 * 
 * 
 * <pre>
 * {
 * "codigoDeOperacion": 0, "mensaje":{ "titulo":"Atencion", "texto" :"Operacion
 * Exitosa", "codigoError":"ERR999", }, "data":{} }
 * </pre>
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Aspect
@Order(Integer.MIN_VALUE + 4)
public class DTOAspect {
    /**
     * logger de la clase
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DTOAspect.class);
    /**
     * entonro de ejecucion
     */
    @Autowired
    private Environment env;
    /**
     * codigo generico de exito
     */
    @Value("${com.mx.santander.commons.message.generic.success.messageCode:0}")
    private int genericSuccessMessageCode;
    /**
     * response de exito
     */
    private ResponseMessageTO message;
    /**
     * prefix para los mensajes en la propiedades
     */
    @Value("${com.mx.santander.commons.message.prefix:message_}")
    private String messagePrefix;
    /**
     * separador de los titulos y mensajes
     */
    @Value("${com.mx.santander.commons.message.split:\\|}")
    private String messageSplit;
    /**
     * paquete a escanear
     */
    @Value("${com.mx.santander.commons.packageScan:com.mx.santander}")
    private String packageScan;

    /**
     * Constructor vacio por default para cumplir con la especificacion y
     * requerimientos de un bean
     * 
     * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
     */
    public DTOAspect() {
        /**
         * Constructor vacio por default para cumplir con la especificacion y
         * requerimientos de un bean
         * 
         * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
         */
        LOGGER.info("Inicia ResponseAspec");
    }

    /**
     * Advice que se encarga de manejar todas las peticiones hechas al
     * microservicios captura todas la peticiones con el point cut donde se
     * establece que deben ser metodos anotados con {@link RequestMapping} y
     * cualquier numero de argumentos, este metodo se encarga de validar si la
     * respuesta del servicio implementa {@link ResponseTO} para asi dar formato de
     * respuesta establecido para el proyecto de GoPay
     * 
     * @param pj
     *            point cut
     * @return retorna {@link ResponseTO}
     * @throws Throwable
     *             exception
     */
    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) &&  args(..)")
    public Object requestWhitoutRequest(ProceedingJoinPoint pj) throws Throwable {
        // validamos si el advice esta dentro los paquetes validos
        if (!pj.getSignature().getDeclaringTypeName().startsWith(packageScan)) {
            LOGGER.debug("El especto no aplica sobre:{}", pj.getSignature().getDeclaringTypeName());
            return pj.proceed(pj.getArgs());
        }
        LOGGER.debug("Se ejecuta DTOAspect");
        // si esta en modo debug imprime el request de la peticion
        if (LOGGER.isTraceEnabled()) {
            Arrays.asList(pj.getArgs()).stream().filter(arg -> arg instanceof RequestTO).findFirst()
                    .ifPresent(arg -> LOGGER.trace("Request:{}", ((RequestTO) arg).toJsonString()));
        }

        Object response = pj.proceed(pj.getArgs());
        /**
         * se valida que la respuesta implemente ResponseTO para asi poder envolverlo en
         * ResponseTOWrapper
         */
        if (response instanceof ResponseTO) {
            ResponseTOWrapper responseTOWrapper = new ResponseTOWrapper(genericSuccessMessageCode, message,
                    (ResponseTO) response);
            LOGGER.trace("Response:{}", responseTOWrapper);
            response = responseTOWrapper;
        }
        return response;
    }

    /**
     * Inicializa el aspecto para configurar el formato de la respuestas extitosa
     * por default, con codigo de operacion 0, todas las respuestas que implenetan
     * {@link ResponseTO} se agregan a {@link ResponseTOWrapper} para asi dat el
     * formato establecido para el poryecto de GoPay
     */
    @PostConstruct
    private void initMessage() {
        LOGGER.debug("Se incializa el mensaje de exito.");
        // validamos el mensaje
        String mensaje = env.getProperty(messagePrefix + genericSuccessMessageCode);
        if (isNull(mensaje) || mensaje.isEmpty()) {
            LOGGER.warn("El Codigo de mensaje:{} no existe en las propiedades.",
                    messagePrefix + genericSuccessMessageCode);
            message = new ResponseTOWrapper.ResponseMessageTO("Atenci\u00F3n", "Operaci\u00F3n Exitosa.", null);
            return;
        }
        LOGGER.debug("Mensaje en properties:{}", mensaje);
        String[] msgSuccess = mensaje.split(messageSplit);
        // validamos si viene titulo y mensaje, o solo titulo
        if (mensaje.length() > 1) {
            message = new ResponseTOWrapper.ResponseMessageTO(msgSuccess[0], msgSuccess[1], null);
            return;
        }
        message = new ResponseTOWrapper.ResponseMessageTO("Atenci\u00F3n", msgSuccess[0], null);
    }

}
