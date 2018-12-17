package com.mx.santander.commons.exceptions;

import java.util.Objects;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.mx.santander.commons.messages.IMessages;
import com.mx.santander.commons.messages.MessageWithOutTraceException;
import com.mx.santander.commons.messages.MessageWithTraceException;
import com.mx.santander.commons.model.dto.ResponseTOWrapper;
import com.mx.santander.commons.model.dto.ResponseTOWrapper.ResponseMessageTO;

/**
 * {@link HandlerExceptions} es la clase encargada de capturar todas las
 * {@link Exception} que se puedan producir en los servicios de la fachada
 * 
 * @author Z465536
 *
 */
@ControllerAdvice
public class HandlerExceptions {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerExceptions.class);
    /**
     * se establece el status HTTP por default
     */
    private static final HttpStatus STATUS = HttpStatus.OK;
    /**
     * enviroment
     */
    @Autowired
    private Environment env;
    // codigo de error generico
    @Value("${com.mx.santander.commons.message.generic.error.code:MSCM0}")
    private String genericErrorCode;
    // mensaje generico
    @Value("${com.mx.santander.commons.message.generic.error.messageCode:-1}")
    private int genericMessageCode;
    // prefix de los mensaje
    @Value("${com.mx.santander.commons.message.prefix:message_}")
    private String messagePrefix;
    // splint entre el titulo de los mensajes y el mensaje
    @Value("${com.mx.santander.commons.message.split:\\|}")
    private String messageSplit;

    /**
     * ExceptionHandler para el manejo de exceptions cuando no se cumplen los
     * {@link Valid} constraint , se basa en las validaciones de la API de
     * especificación JSR-303
     * 
     * @param ex
     *            {@link ConstraintViolationException} capturada
     * @return {@link ResponseTOWrapper}
     * @see https://docs.oracle.com/javaee/6/tutorial/doc/gircz.html
     *
     * @param ex
     *            {@link ConstraintViolationException}
     * @return {@link ResponseTOWrapper}
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseTOWrapper> exceptionHandler(ConstraintViolationException ex) {
        LOGGER.warn("Entra al Handler de ConstraintViolationException:{}", ex.getClass().getName());
        ResponseTOWrapper response = getGenericErrorMessage(ex);
        response.setMensajeDev("");
        ex.getConstraintViolations()
                .forEach(c -> response.setMensajeDev(c.getMessage() + "#-#" + response.getMensajeDev()));
        LOGGER.debug("Response: {}", response);
        return new ResponseEntity<>(response, STATUS);
    }

    /**
     * ExceptionHandler para el manejo de exceptions cuando no se cumplen los
     * {@link Valid} constraint , se basa en las validaciones de la API de
     * especificación JSR-303
     * 
     * @param ex
     *            {@link BindException}
     * @return {@link ResponseTOWrapper}
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseTOWrapper> exceptionHandler(BindException ex) {
        LOGGER.warn("Entra al Handler de BindException:{}", ex.getClass().getName());
        ResponseTOWrapper response = getGenericErrorMessage(ex);
        response.setMensajeDev("");
        ex.getBindingResult().getAllErrors()
                .forEach(e -> response.setMensajeDev(response.getMensajeDev() + e.getDefaultMessage() + " #-# "));
        LOGGER.debug("Response: {}", response);
        return new ResponseEntity<>(response, STATUS);
    }


    /**
     * ExceptionHandler para manejar las exceptions de tipo
     * {@link MessageWithTraceException} y {@link MessageWithOutTraceException} e
     * informar mensajes personalizados y codigos de error a quien consume los
     * servicios, tambien cacha las exceptions de tipo
     * {@link PropagationMessageException} para propagar el mismo codigo de error y
     * numero de mensaje origen
     * 
     * @param ex
     *            {@link IMessages}
     * @return {@link ResponseTOWrapper}
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResponseTOWrapper> exceptionHandler(IllegalStateException ex) {
        if (ex instanceof IMessages) {
            LOGGER.info("Entra al Handler de MessageException:{}", ex.getClass().getName());
            IMessages message = (IMessages) ex;
            ResponseTOWrapper response = getErrorMessage((IMessages) ex);
            if (Objects.nonNull(message.getData())) {
                response.setResponse(message.getData());
            }
            LOGGER.debug(" Response:{}", response);
            return new ResponseEntity<>(response, STATUS);
        } else {
            return exceptionHandlerException(ex);
        }
    }

    /**
     * ExceptionHandler para el manejo de exceptions cuando no se cumplen los
     * {@link Valid} constraint , se basa en las validaciones de la API de
     * especificación JSR-303
     * 
     * @param ex
     *            {@link MethodArgumentNotValidException}
     *            {@link ConstraintViolationException} capturada
     * @return {@link ResponseTOWrapper}
     * @see https://docs.oracle.com/javaee/6/tutorial/doc/gircz.html
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseTOWrapper> exceptionHandler(MethodArgumentNotValidException ex) {
        LOGGER.warn("Entra al Handler de MethodArgumentNotValidException:{}", ex.getClass().getName());
        ResponseTOWrapper response = getGenericErrorMessage(ex);
        response.setMensajeDev("");
        ex.getBindingResult().getAllErrors()
                .forEach(e -> response.setMensajeDev(response.getMensajeDev() + e.getDefaultMessage() + " #-# "));
        LOGGER.debug("Response:{} ", response);
        return new ResponseEntity<>(response, STATUS);
    }

    /**
     * ExceptionHandler para capturar todas las {@link Exception} no controladas y
     * pintar el stackTrace del problema ocurrido, y poder retornar una respuesta
     * informando que ocurrio un problema en el servicio al consumidor de la API
     * 
     * @param ex
     *            {@link Exception}
     * @return {@link ResponseTOWrapper}
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ResponseTOWrapper> exceptionHandlerException(Throwable ex) {
        LOGGER.warn("Entra al Handler de Throwable:{}", ex.getClass().getName());
        ResponseTOWrapper response = getGenericErrorMessage(ex);
        LOGGER.debug("Response:{}", response);
        return new ResponseEntity<>(response, STATUS);

    }

    /**
     * Metodo para generar un objecto de tipo {@link ResponseTOWrapper} donde se
     * informa del problema ocurrido
     * 
     * @param ex
     *            {@link IMessages}
     * @return {@link ResponseTOWrapper}
     */

    private ResponseTOWrapper getErrorMessage(IMessages ex) {
        // se imprime el stack trace solo si la excepcion es MessageWithTraceException
        if (ex instanceof MessageWithTraceException) {
            LOGGER.error("Stacktrace del problema ocurrido", ex);
        }
        return new ResponseTOWrapper(ex.getMessageCode(),
                getMessage(messagePrefix + ex.getMessageCode(), ex.getErrorCode()));
    }

    /**
     * Retorna un {@link ResponseTOWrapper} generico, cuando no se tiene un codigo
     * de error establecido para el problema ocurrido
     * 
     * @param ex
     *            {@link Throwable}
     * @return {@link ResponseTOWrapper}
     */
    private ResponseTOWrapper getGenericErrorMessage(Throwable ex) {
        LOGGER.error("Ocurrio un problema inesperado: ", ex);
        return new ResponseTOWrapper(genericMessageCode,
                getMessage(messagePrefix + genericMessageCode, genericErrorCode));
    }

    /**
     * Metodo para obtener de las propiedades el numero de mensaje a informar en el
     * {@link ResponseTOWrapper} el formato de los mensajes en las propiedades debe
     * ser {@code message_#} donde # es numerico.
     * 
     * @param idMessage
     *            numero de mensaje
     * @param ec
     *            codigo de error
     * @return {@link ResponseTOWrapper}
     */
    private ResponseMessageTO getMessage(String idMessage, String ec) {
        String errorCode = ec;
        LOGGER.debug("Codigo de mensaje:{}", idMessage);

        // validamos el mensaje
        String mensaje = env.getProperty(idMessage);
        if (Objects.isNull(mensaje) || mensaje.isEmpty()) {
            LOGGER.warn("El Codigo de mensaje:{} no existe en las propiedades.", idMessage);
            return new ResponseTOWrapper.ResponseMessageTO("Atenci\u00F3n",
                    "Lamentamos los inconvenientes, intentalo nuevamente.", errorCode);
        }
        LOGGER.debug("Mensaje:{}", mensaje);
        String[] msgError = mensaje.split(messageSplit);
        // validamos si viene titulo y mensaje, o solo titulo
        if (msgError.length > 1) {
            return new ResponseTOWrapper.ResponseMessageTO(msgError[0], msgError[1], errorCode);
        }
        return new ResponseTOWrapper.ResponseMessageTO("Atenci\u00F3n", msgError[0], errorCode);
    }
}
