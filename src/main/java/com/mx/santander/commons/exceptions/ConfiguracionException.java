package com.mx.santander.commons.exceptions;

/**
 * {@link ConfiguracionException} es una clase para ser lanzada cuando en los
 * beans de configuracion sucede algun error o alguna inconcistencia sea lanzada
 * impidiendo el inicio de la aplicacion
 * 
 * @author santander
 *
 */
public class ConfiguracionException extends IllegalStateException {
    private static final long serialVersionUID = 1768877308381311597L;

    /**
     * Constructor con posibilidad de suprimir {@link Throwable} y pintar mensaje de
     * stackTrace del problema ocurrido
     * 
     * @param msg
     *            mensaje log
     * @param ex
     *            {@link Throwable}
     */
    public ConfiguracionException(String msg, Throwable ex) {
        super(msg, ex);
        /**
         * Constructor vacio
         */
    }
}
