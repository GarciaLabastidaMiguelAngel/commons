package com.mx.santander.commons.model.session.entity;

import java.io.Serializable;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Data;

/**
 * Metodo para mantener informacion del usuario en sesion
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Data
public class PrincipalUser implements Principal, Serializable {
    /**
     * version del bean
     */
    private static final long serialVersionUID = 4044935071013212223L;
    /**
     * nombre de atributo con el que se guarda en session
     */
    public static final String ATTRIBUTE_SESSION_NAME = PrincipalUser.class.getName();
    /**
     * id del usuario
     */
    private String userName;
    /**
     * nombre
     */
    private String fistName;
    /**
     * apellido materno
     */
    private String middleName;
    /**
     * apellido paterno
     */
    private String lastName;
    /**
     * roles asignados
     */
    private List<String> roles = new ArrayList<>();
    /**
     * indica si esta habilitado su acceso
     */
    private boolean enable;
    /**
     * indica si su acceso a expirado
     */
    private boolean expireAccess;
    /**
     * fecha de ultimo acceso
     */
    private Date lastAccess;

    /**
     * fecha con formato
     * 
     * @return fecha
     */
    public String getLastAccess() {
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        return dt.format(lastAccess);
    }

    @Override
    public String getName() {
        return userName;
    }

}
