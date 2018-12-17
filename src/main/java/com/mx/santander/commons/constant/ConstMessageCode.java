package com.mx.santander.commons.constant;

/**
 * Condigos de mensaje que puede lanzar api connect, cada Ms en el proyecto de
 * gopay puede lanzar diversos mensajes los cuales estan declarado en gitlab
 * desde donde son cargados
 * 
 * @see https://gitlab.alm.gsnetcloud.corp/gopay-mx/gp-config-service-git/blob/dev/gp-messages.properties
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public final class ConstMessageCode {
    /**
     * codigo de error generico
     */
    public static final int ERROR_GENERICO = -1;
    /**
     * sesion expirada
     */
    public static final int SESSION_EXPIRADA = -1000;
    /**
     * role de sesion no autorizado
     */
    public static final int SESSION_ROLE_NO_AUTORIZADO = -1001;
    /**
     * canal no autorizado
     */
    public static final int ACCESO_DENEGADO_CANAL = -2000;
    /**
     * fuera de servicio
     */
    public static final int ACCESO_CANAL_FUERA_SERVICIO = -2001;

    /**
     * Constructor vacio por default para cumplir con la especificacion y
     * requerimientos de un bean
     * 
     * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
     */
    private ConstMessageCode() {
        /**
         * Constructor vacio por default para cumplir con la especificacion y
         * requerimientos de un bean
         * 
         * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
         */
    }
}
