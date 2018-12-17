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
 * Anotacion que se encarga de validar si un buc o id de cliente es valido, el
 * id del cliente debe ser de 8 digitos
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Pattern(regexp = Buc.PATTERN, flags = Pattern.Flag.CASE_INSENSITIVE)
public @interface Buc {
    /**
     * patron de la estructura del buc
     */
    String PATTERN = "\\d{8}";

    /**
     * mensaje de error
     * 
     * @return
     */
    String message() default "El id del cliente debe ser de 8 digitos";

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
