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
 * Anotacion que se encarga de validar si el tipo de la referencia cumple con la
 * logitud de 5 a 60 caracteres permitidos
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Pattern(regexp = Reference.PATTERN, flags = Pattern.Flag.CASE_INSENSITIVE)
public @interface Reference {
    /**
     * patron de la estructura de un numero de refrencia
     */
    String PATTERN = "\\w{5,60}";

    /**
     * mensaje de error
     * 
     * @return
     */
    String message() default "El numero de referencia del servicio debe ser entre 5 a 60 caracteres";

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
