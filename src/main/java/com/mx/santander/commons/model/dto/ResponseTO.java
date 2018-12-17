package com.mx.santander.commons.model.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.mx.santander.commons.utils.JacksonUtils;

/**
 * los Bean del proyecto de GoPay que valla a servir como respuesta en un
 * servicio debe implementar dicha interfaz para que cumpla con el estandar de
 * tranferencia de informacion entre microservicios ademas de contener un metodo
 * serializador{@link #toJsonString()}
 * 
 * @author santander
 *
 */
@JsonInclude(Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface ResponseTO extends Serializable {
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
                    "Error en el serealizado de ResponseTO", e);
        }
    }
}
