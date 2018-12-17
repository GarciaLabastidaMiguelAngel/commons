package com.mx.santander.commons.channel.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Anotacion para validar si la peticion del canal esta dentro el horario de
 * servicio, los servicios que requieran de esta validación deben estar
 * acompañados de la anotaion {@link RequestMapping}
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NoValidateChannelOfService {

}
