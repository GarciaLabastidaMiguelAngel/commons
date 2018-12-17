package com.mx.santander.commons.autoconfigurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;

import com.mx.santander.commons.constant.ConstCommons;
import com.mx.santander.commons.session.security.aspec.SesionAspect;
import com.mx.santander.commons.session.security.aspec.SesionRoleAspect;

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
@ConditionalOnProperty(prefix = "com.mx.santander.commons.session", name = "enable", havingValue = "true")
public class SessionAutoConfiguration {
    /**
     * logger de la clase
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SessionAutoConfiguration.class);

    /**
     * Clase de configuracion para conexion con redis y manejo de sesion a travez de
     * redis
     * 
     * @author santander
     *
     */
    @Configuration
    @EnableRedisHttpSession
    public class EnableRedis {

        /**
         * {@link HttpSessionStrategy} es para indicar si la sesion se majera por Cookie
         * o por header como es el caso de esta configuracion, spring por default la
         * maneja por Cookie por lo cual debemos redefinir la estrategia
         * 
         * @return {@link HttpSessionStrategy}
         */
        @Bean
        @Primary
        public HttpSessionStrategy httpSessionStrategy() {
            LOGGER.info("Se Inicializa HeaderHttpSessionStrategy con header:{}", ConstCommons.SESSION_HEADER);
            HeaderHttpSessionStrategy headerHttpSessionStrategy = new HeaderHttpSessionStrategy();
            headerHttpSessionStrategy.setHeaderName(ConstCommons.SESSION_HEADER);
            return headerHttpSessionStrategy;
        }

        /**
         * {@link JedisConnectionFactory} se configura el cliente de conexiones redis
         * 
         * @return {@link JedisConnectionFactory}
         */
        @Bean
        @ConfigurationProperties(prefix = "com.mx.santander.commons.redis")
        @Primary
        public JedisConnectionFactory jedisConnectionFactory() {
            LOGGER.info("Se inicializa JEDIS-REDIS");
            return new JedisConnectionFactory();
        }

        /**
         * Se incializa {@link SesionAspect}
         * 
         * @return {@link SesionAspect}
         */
        @Bean
        public SesionAspect sessionAspec() {
            return new SesionAspect();
        }

        /**
         * Se incializa {@link SesionRoleAspect}
         * 
         * @return {@link SesionRoleAspect}
         */
        @Bean
        public SesionRoleAspect sessionRoleAspec() {
            return new SesionRoleAspect();
        }
    }

}
