package com.mx.santander.commons.messages;

import java.util.Objects;

import com.mx.santander.commons.model.dto.ResponseTO;

/**
 * Esta Clase es para mandar mensajes con stackTrace, mayormente funciona para
 * enviar mensajes y envolver {@link Exception} para tener la traza del problema
 * ocurrido
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public class MessageWithTraceException extends IllegalStateException implements IMessages {
    /**
     * Clase para lanzar mensajes cuando el flujo no puede continuar, se pinta la
     * excepción en consola
     */
    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final int messageCode;

    /**
     * Constructor para imprimir mensaje en el log de las exceptions
     * 
     * @param messageCode
     *            numero de mensaje
     * @param errorCode
     *            codigo de error
     * @param msgLog
     *            mensaje a pintar en el stackTrace
     * @param ex
     *            exception que se envuelve
     */
    public MessageWithTraceException(int messageCode, String errorCode, Exception ex) {
        super(ex);
        if (messageCode >= 0) {
            throw new IllegalStateException("El messageCode debe ser menor a 0 ya que se trata de un ERROR");
        }
        if (Objects.isNull(errorCode) || errorCode.isEmpty()) {
            throw new IllegalStateException("El codigo de error no puede ser nulo o vacio");
        }
        this.errorCode = errorCode.toUpperCase();
        this.messageCode = messageCode;
    }

    /**
     * Constructor con posibilidad de suprimir {@link Throwable} y pintar mensaje de
     * stackTrace del problema ocurrido
     * 
     * @param messageCode
     *            numero de mensaje
     * @param errorCode
     *            codigo de error
     * @param msgLog
     *            mensaje para imprimir en el stackTrace
     * @param ex
     *            Throwable a suprimir
     */
    public MessageWithTraceException(int messageCode, String errorCode, String msgLog, Exception ex) {
        super(msgLog, ex);
        if (Objects.isNull(ex)) {
            throw new IllegalStateException("El Exception no puede ser nula");
        }
        if (messageCode >= 0) {
            throw new IllegalStateException("El messageCode debe ser menor a 0 ya que se trata de un ERROR");
        }
        if (Objects.isNull(errorCode) || errorCode.isEmpty()) {
            throw new IllegalStateException("El codigo de error no puede ser nulo o vacio");
        }
        if (Objects.isNull(msgLog) || msgLog.isEmpty()) {
            throw new IllegalStateException("El msgLog no puede ser nulo o vacio");
        }

        this.errorCode = errorCode.toUpperCase();
        this.messageCode = messageCode;
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

}
