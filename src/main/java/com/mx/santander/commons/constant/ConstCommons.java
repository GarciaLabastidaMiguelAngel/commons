package com.mx.santander.commons.constant;

/**
 * Constantes que se ocupan en la libreria de commons
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public final class ConstCommons {
    /**
     * Header que contiene el id de sesion
     */
    public static final String SESSION_HEADER = "x-auth-token";
    /**
     * header donde se debe informar el canal que consume
     */
    public static final String XCHANNEL = "x-channel";

    /**
     * Constructor vacio por default para cumplir con la especificacion y
     * requerimientos de un bean
     * 
     * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
     */
    private ConstCommons() {
        /**
         * Constructor vacio por default para cumplir con la especificacion y
         * requerimientos de un bean
         * 
         * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
         */
    }
}
