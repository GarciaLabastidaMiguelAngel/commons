package com.mx.santander.commons.autoconfigurations;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mx.santander.commons.channel.security.aspec.ChannelAccessAspect;
import com.mx.santander.commons.channel.security.aspec.ChannelHoursOfServiceAspect;

/**
 * Clase para configurar el acceso por canal a los servicios
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Configuration
@ConditionalOnProperty(prefix = "com.mx.santander.commons.channel.access", name = "enable", havingValue = "true")
public class ChannelAutoConfiguration {
    /**
     * Se incializa {@link ChannelAccessAspect} para validar que solo canales
     * permitidos consuman la API
     * 
     * @return {@link ChannelAccessAspect}
     */
    @Bean
    public ChannelAccessAspect accessAspect() {
        return new ChannelAccessAspect();
    }

    /**
     * Aspecto para determinar si el canal se encuentra dentro del horario de
     * servicio para consumir servicio
     * 
     * @return {@link ChannelHoursOfServiceAspect}
     */
    @Bean
    public ChannelHoursOfServiceAspect accessServiceAspec() {
        return new ChannelHoursOfServiceAspect();
    }
}
