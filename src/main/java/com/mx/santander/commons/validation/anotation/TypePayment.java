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
 * Anotacion que se encarga de validar si el tipo de pago informado es UNI=
 * unico o REC recurrente
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {})
@ReportAsSingleViolation
@Pattern(regexp = TypePayment.PATTERN, flags = Pattern.Flag.CASE_INSENSITIVE)
public @interface TypePayment {
    /**
     * patron de la estructura de un numero de cuenta
     */
    String PATTERN = "^UNI$|^REC$";

    /**
     * mensaje de error
     * 
     * @return
     */
    String message() default "El(Los) valor(es) de tipo de pago solo puede(n) ser UNI o REC";

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
