package com.mx.santander.commons.postprocesor;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.mx.santander.commons.interceptor.CustomClientHttpRequestInterceptor;
import com.mx.santander.commons.interceptor.CustomHttpHeadersPropagationInterceptor;

/**
 * {@link RestTemplateBeanPostProcessor} hereda de {@link BeanPostProcessor}
 * para que toda creacion de beans pase por esta clase despues de haber sido
 * creado, esta clase valida si el bean creado por Spring Framework es de tipo
 * {@link RestTemplate} para poder agregar
 * {@link CustomClientHttpRequestInterceptor} y capturar todas las peticiones a
 * otros servicios
 * 
 * @see https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/beans/factory/config/BeanPostProcessor.html
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public class RestTemplateBeanPostProcessor implements BeanPostProcessor {
    /**
     * logger de la clase
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(RestTemplateBeanPostProcessor.class);
    /**
     * indicador para habilitar la propagacion de headers
     */
    @Value("${com.mx.santander.headers.propagation.enable:true}")
    private boolean propagationHeaders;
    /**
     * headers a ignorar en la propagacion
     */
    @Value("#{'${com.mx.santander.headers.propagation.exclude:CorID}'.split(',')}")
    private List<String> ignorePropagationHeaders;

    /**
     * bandera de debug de headers en el cliente
     */
    @Value("${com.mx.santander.headers.propagation.trace.debug:false}")
    private boolean traceHeadersDebug;

    /**
     * Metodo por donde pasan todo los bean inicializados despues del setter de
     * todas sus propiedades, es aqui donde agregamos
     * {@link CustomClientHttpRequestInterceptor} a todos los beans creados del tipo
     * {@link RestTemplate}
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String name) {
        if (bean instanceof RestTemplate) {
            RestTemplate restTemplate = (RestTemplate) bean;
            SimpleClientHttpRequestFactory requestFactoryClient = new SimpleClientHttpRequestFactory();
            requestFactoryClient.setOutputStreaming(false);
            ClientHttpRequestFactory requestFactory = new BufferingClientHttpRequestFactory(requestFactoryClient);
            restTemplate.setRequestFactory(requestFactory);
            if (propagationHeaders) {
                LOGGER.debug(
                        "Se agrega propagacion de headers a los bean RestTemplate, headers a ignorar en la propagacion de headers:{}",
                        ignorePropagationHeaders);
                CustomHttpHeadersPropagationInterceptor headersPropagationInterceptor = new CustomHttpHeadersPropagationInterceptor();
                // se agregan los headers a ignorar en la propagacion
                headersPropagationInterceptor.setIgnorePropagationHeaders(ignorePropagationHeaders);
                // se agrega bandera de debug
                headersPropagationInterceptor.setTraceHeadersDebug(traceHeadersDebug);
                restTemplate.getInterceptors().add(headersPropagationInterceptor);
            }
            LOGGER.debug("Se agrega CustomClientHttpRequestInterceptor al bean:{}", name);
            restTemplate.getInterceptors().add(new CustomClientHttpRequestInterceptor());

        }
        return bean;
    }

    /**
     * Por este metodo pasan todos los beans creados antes de ser inicializados
     * todos sus propiedades
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) {
        return bean;
    }

}
