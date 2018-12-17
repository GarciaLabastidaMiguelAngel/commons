package com.mx.santander.commons.constant;

/**
 * Constantes de codigos de error que puede lanzar Api connect cada codigo de
 * error delcarado en el proyecto de GoPay se encuentra descrito en gitLab
 * 
 * @see https://gitlab.alm.gsnetcloud.corp/gopay-mx/gp-config-service-git
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public final class ConstErrorCode {
    /**
     * acceso denegado al canal
     */
    public static final String ACCESO_DENEGADO_CANAL = "MSCS02";
    /**
     * acceso denegado al canal
     */
    public static final String ACCESO_CANAL_FUERA_SERVICIO = "MSCS04";
    /**
     * session expirada
     */
    public static final String SESSION_EXPIRADA = "MSCS01";
    /**
     * role no autorizado
     */
    public static final String SESSION_ROLE_NO_AUTORIZADO = "MSCS03";

    /**
     * Constructor vacio por default para cumplir con la especificacion y
     * requerimientos de un bean
     * 
     * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
     */
    private ConstErrorCode() {
        /**
         * Constructor vacio por default para cumplir con la especificacion y
         * requerimientos de un bean
         * 
         * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
         */
    }
}
