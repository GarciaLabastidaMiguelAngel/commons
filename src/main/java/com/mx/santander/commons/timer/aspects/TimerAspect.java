package com.mx.santander.commons.timer.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mx.santander.commons.model.dto.ResponseTO;

/**
 * Aspecto que camptura las peticiones al microservicio y da formato a las
 * respuesta de los servicios que se encuentren dentro de los paquetes
 * especificados por {@link #packageScan}, de lo contrario no da formato a las
 * respuestas
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Aspect
@Order(Integer.MIN_VALUE + 1)
public class TimerAspect {
    /**
     * logger de la clase
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TimerAspect.class);
    /**
     * paquete a escanear
     */
    @Value("${com.mx.santander.commons.packageScan:com.mx.santander}")
    private String packageScan;

    /**
     * Constructor vacio por default para cumplir con la especificacion y
     * requerimientos de un bean
     * 
     * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
     */
    public TimerAspect() {

        /**
         * Constructor vacio por default para cumplir con la especificacion y
         * requerimientos de un bean
         * 
         * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
         */
        LOGGER.info("Inicia Aspecto Timer para medir tiempos de respuestas");
    }

    /**
     * Aspector para validar los tiempos de respuesta de los MS
     * 
     * @param pj
     *            point cut
     * @param requestMapping
     *            {@link RequestMapping}
     * @return {@link ResponseTO}
     * @throws Throwable
     *             exception
     */
    @Around("@annotation(requestMapping) &&  args(..)")
    public Object requestWhitoutRequest(ProceedingJoinPoint pj, RequestMapping requestMapping) throws Throwable {
        LOGGER.debug("Se ejecuta Aspecto para medir tiempos de respuesta");
        // validamos si el advice esta dentro los paquetes validos
        long timeStart = System.currentTimeMillis();
        /**
         * Obtenemos el path invocado
         */
        String[] paths;
        if (requestMapping.value().length > 0) {
            paths = requestMapping.value();
        } else {
            paths = requestMapping.path();
        }
        LOGGER.info("Inicia el servicio:{}", paths, "");
        Object response;
        response = pj.proceed(pj.getArgs());
        long timeEnd = System.currentTimeMillis();
        LOGGER.info("El servicio:{} termino en: {} ms", paths, timeEnd - timeStart);
        return response;
    }

}
