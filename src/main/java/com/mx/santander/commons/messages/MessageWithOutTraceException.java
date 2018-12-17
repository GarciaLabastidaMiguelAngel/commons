package com.mx.santander.commons.messages;

import java.util.Objects;

import com.mx.santander.commons.model.dto.ResponseTO;

/**
 * {@link MessageWithOutTraceException} es usada cuando es necesario informar un
 * mensaje y codigo de error donde no se va a pintar nigun stackTrace en consola
 * o logs, no es necesario envolver ningun {@link Exception}
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public class MessageWithOutTraceException extends IllegalStateException implements IMessages {

    /**
     * Clase para lanzar mensajes cuando el flujo no puede continuar, no se pinta la
     * excepción en consola
     */
    private static final long serialVersionUID = 1L;
    /**
     * codigo de error
     */
    private final String codigoError;
    /**
     * codigo mensaje
     */
    private final int codigoMensaje;
    /**
     * datos del servicio
     */
    private final ResponseTO data;

    /**
     * Constructor para enviar mensaje
     * 
     * @param messageCode
     *            numero de mensaje a enviar
     * @param errorCode
     *            codigo de error
     */
    public MessageWithOutTraceException(int messageCode, String errorCode) {
        if (messageCode >= 0) {
            throw new IllegalStateException("El messageCode debe ser menor a 0 ya que se trata de un ERROR");
        }
        if (Objects.isNull(errorCode) || errorCode.isEmpty()) {
            throw new IllegalStateException("El codigo de error no puede ser nulo o vacio");
        }
        this.codigoError = errorCode.toUpperCase();
        this.codigoMensaje = messageCode;
        this.data = null;
    }

    /**
     * Constructor para generar respuestas de advertencia ya que contiene un codigo
     * de advertencia y codigo de mensje @param messageCode mayor a 0 pero tambien
     * contiene informacion de respuesta
     * 
     * @param messageCode
     *            numero de mensaje
     * @param errorCode
     *            codigo de error
     * @param response
     *            {@link ResponseTO}
     */
    public MessageWithOutTraceException(int messageCode, String errorCode, ResponseTO response) {
        if (messageCode <= 0) {
            throw new IllegalStateException("El messageCode debe ser mayor a 0 ya que se trata de un WARNING");
        }
        if (Objects.isNull(errorCode) || errorCode.isEmpty()) {
            throw new IllegalStateException("El codigo de error no puede ser nulo o vacio");
        }
        if (Objects.isNull(response)) {
            throw new IllegalStateException("El response no puede ser null");
        }

        this.codigoError = errorCode.toUpperCase();
        this.codigoMensaje = messageCode;
        this.data = response;
    }

    @Override
    public String getErrorCode() {
        return codigoError;
    }

    @Override
    public int getMessageCode() {
        return codigoMensaje;
    }

    @Override
    public ResponseTO getData() {
        return data;
    }

}
