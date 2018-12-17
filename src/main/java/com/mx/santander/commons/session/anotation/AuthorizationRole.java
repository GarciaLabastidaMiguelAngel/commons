/**
 * 
 */
package com.mx.santander.commons.session.anotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Anotacion para marcar los metodos de las capas de controladores indicando los
 * roles que tendran acceso al servicio solicitado
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface AuthorizationRole {
    /**
     * lista de roles que tendran acceso al servicio
     * 
     * @return array de roles permitidos
     */
    String[] value() default {};
}
