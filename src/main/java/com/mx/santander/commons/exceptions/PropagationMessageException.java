package com.mx.santander.commons.exceptions;

import com.mx.santander.commons.interceptor.CustomClientHttpRequestInterceptor;
import com.mx.santander.commons.messages.IMessages;
import com.mx.santander.commons.model.dto.ResponseTO;

/**
 * Exception que se encarga de se propagada entre los microservicios de Go Pay,
 * esta exception es lanzada en su mayoria de veces por
 * {@link CustomClientHttpRequestInterceptor} quien se encarga de validar las
 * respuestas de los microservicios
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public class PropagationMessageException extends IllegalStateException implements IMessages {

    /**
     * Version de la clase
     */
    private static final long serialVersionUID = 3475544172790777537L;
    private final String errorCode;
    private final int messageCode;
    private final String textMessage;

    /**
     * Constructor que envuelve el codigo de error y numero de mensaje reportado
     * desde otro microservicio invocado
     * 
     * @param messageCode
     *            numero de mensaje
     * @param errorCode
     *            codigo de error
     * @param msgLog
     *            mensaje log
     */
    public PropagationMessageException(int messageCode, String errorCode, String textMessage, String msgLog) {
        super(msgLog);
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.textMessage = textMessage;
    }

    /**
     * Constructor que envuelve el codigo de error y numero de mensaje reportado
     * desde otro microservicio invocado,con la posibilidad de envolver alguna
     * exception ocurrida en el llamado al microservicio
     * 
     * @param messageCode
     *            numero de mensaje
     * @param errorCode
     *            codigo de error
     * @param msgLog
     *            mensaje log
     * @param ex
     *            {@link Throwable}
     */
    public PropagationMessageException(int messageCode, String errorCode, String textMessage, String msgLog,
            Exception ex) {
        super(msgLog, ex);
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.textMessage = textMessage;
    }

    /**
     * Constructor que envuelve el codigo de error y numero de mensaje reportado
     * desde otro microservicio invocado, con la posibilidad de envolver alguna
     * exception ocurrida en el llamado al microservicio
     * 
     * @param messageCode
     *            numero de mensaje
     * @param errorCode
     *            codigo de error
     * @param ex
     *            {@link Throwable}
     */
    public PropagationMessageException(int messageCode, String errorCode, String textMessage, Exception ex) {
        super(ex);
        this.errorCode = errorCode;
        this.messageCode = messageCode;
        this.textMessage = textMessage;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public int getMessageCode() {
        return messageCode;
    }

    @Override
    public ResponseTO getData() {
        return null;
    }

    public String getTextMessage() {
        return textMessage;
    }

}
