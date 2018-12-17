package com.mx.santander.commons.autoconfigurations;

import java.util.concurrent.Executor;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.mx.santander.commons.dto.aspect.DTOAspect;
import com.mx.santander.commons.interceptor.CustomClientHttpRequestInterceptor;
import com.mx.santander.commons.postprocesor.RestTemplateBeanPostProcessor;
import com.mx.santander.commons.timer.aspects.TimerAspect;

/**
 * Clase de Autoconfiguración, esta clase es declara en el archivo
 * src/main/resources/META-INF/spring.factories para que sea escaneada de forma
 * automatica por cualquier proyecto Spring Boot donde sea declarado como
 * dependencia.
 * 
 * 
 * @see https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html
 * @author Miguel Angel Garcia Labastida
 *
 */
@Configuration
@ComponentScan("com.mx.santander.commons")
public class CommonsAutoConfiguration {
    /**
     * se inicializa bean para agregar {@link CustomClientHttpRequestInterceptor}
     * todos los {@link RestTemplate} para capturar todas las peticiones a otros
     * servicios se declara como static ya que se debe inicializar antes que todos
     * los beans declarados
     * 
     * @return {@link BeanPostProcessor}
     */
    @Bean
    public static RestTemplateBeanPostProcessor getRestTemplateBeanPostProcessor() {
        return new RestTemplateBeanPostProcessor();
    }

    /**
     * Executor manejo de procesos asincronos
     * 
     * @return {@link Executor}
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "com.mx.santander.commons.executor")
    public Executor commonsExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(100);
        executor.setThreadNamePrefix("gopay-thread-");
        executor.initialize();
        return executor;
    }

    /**
     * Se incializa {@link DTOAspect} para capturar los request y response de los
     * servicios
     * 
     * @return {@link DTOAspect}
     */

    @Bean
    @ConditionalOnProperty(prefix = "com.mx.santander.commons.service.dto", name = "enable", havingValue = "true", matchIfMissing = true)
    public DTOAspect dtoAspect() {
        return new DTOAspect();
    }

    /**
     * se inicializa {@link TimerAspect} para medir tiempos de respuesta de los
     * servicios
     * 
     * @return {@link TimerAspect}
     */
    @Bean
    @ConditionalOnProperty(prefix = "com.mx.santander.commons.service.timer", name = "enable", havingValue = "true", matchIfMissing = true)
    public TimerAspect timerAspect() {
        return new TimerAspect();
    }

    /**
     * se crea cliente REST a menos que no exista
     * 
     * @return {@link RestTemplate}
     */
    @Bean
    @ConditionalOnMissingBean(value = RestTemplate.class)
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
