package com.mx.santander.commons.validation.anotation;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.Pattern;

/**
 * Anotacion que se encarga de validar si un numero de tarjeta es valido , se
 * hace herencia de {@link Pattern} para generar la validacion con un regex
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Pattern(regexp = Card.PATTERN, flags = Pattern.Flag.CASE_INSENSITIVE)
public @interface Card {
    /**
     * patron de la estructura de un numero de tarjeta
     */
    String PATTERN = "\\d{16}";

    /**
     * mensaje de error
     * 
     * @return
     */
    String message() default "El numero de tarjeta debe de ser de 16 digitos";

    /**
     * grupos
     * 
     * @return {@link Class}
     */
    Class<?>[] groups() default {};

    /**
     * payload
     * 
     * @return {@link Class}
     */
    Class<? extends Payload>[] payload() default {};

}
