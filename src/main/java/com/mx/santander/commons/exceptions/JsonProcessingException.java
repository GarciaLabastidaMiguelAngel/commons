package com.mx.santander.commons.exceptions;

import java.io.IOException;

/**
 * {@link JsonProcessingException} es una exception para seguir el modelo de
 * programacion de Spring Framework, se encarga de envolver
 * {@link com.fasterxml.jackson.core.JsonProcessingException} que es una
 * exception check por una exception no check, o cualquier otra exception
 * lanzada por Jackson
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public class JsonProcessingException extends IllegalStateException {
    /**
     * {@link #serialVersionUID} Version de clase
     */
    private static final long serialVersionUID = -35342942588457L;

    /**
     * contructor para envolver cualquier Exception lanzada por Jackson, todas las
     * Exceptions de Jackson heredan de {@link IOException}
     * 
     * @param e
     *            {@link IOException}
     */
    public JsonProcessingException(IOException e) {
        super(e);
    }

    /**
     * contructor para envolver cualquier Exception lanzada por Jackson, todas las
     * Exceptions de Jackson heredan de {@link IOException}, el parametro msg recibe
     * un String con la descripcion de lo ocurrido.
     * 
     * @param msg
     *            {@link String}
     * @param e
     *            {@link IOException}
     */
    public JsonProcessingException(String msg, IOException e) {
        super(msg, e);
    }
}
