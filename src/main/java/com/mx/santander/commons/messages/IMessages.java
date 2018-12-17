package com.mx.santander.commons.messages;

import com.mx.santander.commons.model.dto.ResponseTO;

/**
 * Interfaz con los metodos necesarios para el manejo de mensajes cuando ocurre
 * un error en la aplicacion o cuando se hace una validacion y no cumple con las
 * reglas de negocio
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public interface IMessages {
    /**
     * Metodo que retorna el codigo de error
     * 
     * @return codigo de error
     */
    String getErrorCode();

    /**
     * metodo que devuelve el numero de mensaje
     * 
     * @return numero de mensaje
     */
    int getMessageCode();

    /**
     * metodo que devuelve un {@link ResponseTO} esto en caso de que
     * {@link #getMessageCode()} sea mayor que 0
     * 
     * @return body response Data
     */
    ResponseTO getData();

}
