package com.mx.santander.commons.utils;

import java.util.Objects;

import com.mx.santander.commons.messages.MessageWithOutTraceException;
import com.mx.santander.commons.model.dto.ResponseTO;

/**
 * Clase de utilidades para mandar mensajes de errores con validaciones comunes
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public final class ObjectsUtils {
    /**
     * constructor privado para no generar instancias
     */
    private ObjectsUtils() {

    }

    /**
     * metodo para validar si el objeto es null envia mensaje de respuesta al
     * servicio
     * 
     * @param obj
     *            objecto a validar
     * @param numeroMensaje
     *            numero de mensaje a enviar
     * @param codigoError
     *            codigo de error
     */
    public static void isNull(Object obj, int numeroMensaje, String codigoError) {
        if (Objects.isNull(obj)) {
            throw new MessageWithOutTraceException(numeroMensaje, codigoError);
        }
    }

    /**
     * metodo para validar si el objeto es null envia mensaje de respuesta al
     * servicio
     * 
     * @param obj
     *            objecto a validar
     * @param numeroMensaje
     *            numero de mensaje a enviar debe ser mayor a 0
     * @param codigoError
     *            codigo de error
     * @param response
     *            cuando existe una respuesta
     */
    public static void isNull(Object obj, int numeroMensaje, String codigoError, ResponseTO response) {
        if (Objects.isNull(obj)) {
            throw new MessageWithOutTraceException(numeroMensaje, codigoError, response);
        }
    }
}
