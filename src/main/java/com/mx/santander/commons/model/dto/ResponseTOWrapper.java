package com.mx.santander.commons.model.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mx.santander.commons.utils.JacksonUtils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Es el Objeto del estandar de respuesta a los servicios de Go Pay donde data
 * sera el objeto que implemente {@link ResponseTO}
 * 
 * @author Z465536
 *
 */
@Data
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseTOWrapper implements ResponseTO, Serializable {

    /**
     * version de clase
     */
    private static final long serialVersionUID = 8410443172188764186L;
    private Integer codigoDeOperacion;
    @Setter(value = AccessLevel.NONE)
    @Getter(value = AccessLevel.NONE)
    private ResponseTO response;
    private String folioDeOperacion;
    private ResponseMessageTO mensaje;
    private String mensajeDev;

    /**
     * Clase que se encarga de modelar el mensaje que se devuelve en la invocacion a
     * los servicios contiene un titulo, codigo de error si es que lo hay y un
     * mensaje
     * 
     * @author Miguel Angel Garcia Labastida
     *
     */
    @Data
    @JsonInclude(Include.NON_NULL)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ResponseMessageTO implements Serializable {
        /**
         * {@link #serialVersionUID}
         */
        private static final long serialVersionUID = 241450169186636769L;
        private String codigoError;
        private String texto;
        private String titulo;

        /**
         * Constructor vacio por default para cumplir con la especificacion y
         * requerimientos de un bean
         * 
         * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
         */
        public ResponseMessageTO() {
            /**
             * Constructor vacio por default para cumplir con la especificacion y
             * requerimientos de un bean
             * 
             * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
             */
        }

        /**
         * Constructor para generar el mensaje u codigo de error si se informo
         * 
         * @param title
         *            titulo
         * @param message
         *            mensaje
         * @param codigoError
         *            codigo de error
         */
        public ResponseMessageTO(String title, String message, String codigoError) {
            this.titulo = title;
            this.texto = message;
            this.codigoError = codigoError;
        }
    }

    /**
     * Constructor vacio por default para cumplir con la especificacion y
     * requerimientos de un bean
     * 
     * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
     */
    public ResponseTOWrapper() {
        codigoDeOperacion = null;
    }

    /**
     * Constructor para armar la respuesta generica estandar de Go Pay
     * 
     * @param codigoDeOperacion
     *            numero mensaje
     * @param message
     *            data
     */
    public ResponseTOWrapper(Integer codigoDeOperacion, ResponseMessageTO message) {
        this.codigoDeOperacion = codigoDeOperacion;
        this.mensaje = message;
        folioDeOperacion = UUID.randomUUID().toString();
    }

    /**
     * Constructor para armar la respuesta generica estandar de Go Pay
     * 
     * @param codigoDeOperacion
     *            codigo de operacion
     * @param message
     *            mensaje de la operacion
     * @param data
     *            data
     */
    public ResponseTOWrapper(Integer codigoDeOperacion, ResponseMessageTO message, ResponseTO data) {
        this.codigoDeOperacion = codigoDeOperacion;
        this.mensaje = message;
        this.response = data;
        folioDeOperacion = UUID.randomUUID().toString();
    }

    public ResponseTO getData() {
        if (Objects.isNull(response)) {
            return new ResponseTO() {

                /**
                 * 
                 */
                private static final long serialVersionUID = 7982026829081929959L;
            };
        }
        return response;
    }

    public void setResponse(ResponseTO response) {
        this.response = response;
    }

    @Override
    public String toString() {
        try {
            return JacksonUtils.JACKSON.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new com.mx.santander.commons.exceptions.JsonProcessingException(
                    "Error en el serealizado de ResponseTOWrapper", e);
        }
    }

}
