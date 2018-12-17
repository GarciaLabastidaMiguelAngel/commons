package com.mx.santander.commons.session.anotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Anotacion para omitir la validacion de sesión(cuando la sesión esta activa)
 * esta anotacion debe ir acompañada de {@link RequestMapping} y se debe
 * declarar a nivel de Controller para omitir la validacion de la sesion no
 * funciona en otra parte que no sea en los controller
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface NonValidateSession {

}
