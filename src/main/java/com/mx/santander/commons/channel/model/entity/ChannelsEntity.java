package com.mx.santander.commons.channel.model.entity;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Entidad para hacer el Mapping con la colecion en mongo de channels y traer la
 * informacion necesaria para saber si tienen acceso a consumir los servicios o
 * no
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Document(collection = "channels")
@Data
public class ChannelsEntity implements Serializable {
    /**
     * version de clase
     */
    private static final long serialVersionUID = -8017516048086019290L;
    /**
     * id mongo
     */
    @Id
    private String id;
    /**
     * canal
     */
    private String canal;
    /**
     * nombre del canal
     */
    private String nombre;
    /**
     * descripcion del canal
     */
    private String descripcion;
    /**
     * horario de servicio del canal
     */
    private HorarioServicio horarioServicio;
    /**
     * indica si esta activo
     */
    private boolean activo;

    /**
     * Clase para mapear los horarios de servicio del canal
     * 
     * @author Miguel Angel Garcia Labastida
     *
     */
    @Data
    public static class HorarioServicio implements Serializable {
        /**
         * version del bean
         */
        private static final long serialVersionUID = -7394860317113530264L;
        /**
         * dias de la semana
         */
        private List<String> dias;
        /**
         * hora de inicio
         */
        private String horaInicio;
        /**
         * hora de fin
         */
        private String horaFin;

    }
}
