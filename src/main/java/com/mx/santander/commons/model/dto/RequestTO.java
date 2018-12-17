package com.mx.santander.commons.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mx.santander.commons.utils.JacksonUtils;

/**
 * los Bean que requiera ser expuesto en un servicio de GoPay es necesaario que
 * implemente dicha interfaz para la solicitud sea bindeada al objecto ademas de
 * contener un metodo serializador{@link #toJsonString()}
 * 
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface RequestTO {

    /**
     * Metodo para hacer paseo del Object a JSON con ayuda de {@link JacksonUtils}
     * 
     * @return json string
     */
    default String toJsonString() {
        try {
            return JacksonUtils.JACKSON.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new com.mx.santander.commons.exceptions.JsonProcessingException(
                    "Error en el serealizado de RequestTO", e);
        }
    }
}
