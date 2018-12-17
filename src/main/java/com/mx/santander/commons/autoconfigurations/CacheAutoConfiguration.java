package com.mx.santander.commons.autoconfigurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

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
@ConditionalOnProperty(prefix = "com.mx.santander.commons.cache", name = "enable", havingValue = "true")
public class CacheAutoConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheAutoConfiguration.class);

    /**
     * Clase de Autoconfiguración, esta clase es declara en el archivo
     * src/main/resources/META-INF/spring.factories para que sea escaneada de forma
     * automatica por cualquier proyecto Spring Boot donde sea declarado como
     * dependencia.
     * 
     * @author Miguel Angel Garcia Labastida
     *
     */
    @Configuration
    @EnableCaching
    public class EnableCache {
        /**
         * {@link CacheManager} encargado del manejo de la cache en Spring boot, se
         * incializa con un {@link RedisTemplate} para que el manejo de la cache sea
         * desde redis
         * 
         * @param redisTemplate
         *            {@link RedisTemplate}
         * @return {@link CacheManager}
         */
        @Bean
        @Primary
        @ConfigurationProperties(prefix = "com.mx.santander.commons.cache")
        public CacheManager cacheManager(RedisTemplate<Object, Object> redisTemplate) {
            return new RedisCacheManager(redisTemplate);
        }

        /**
         * El bean primario RedisConnectionFactory esta en
         * {@link SessionAutoConfiguration}, si este no es creado se toma este para la
         * conexion con redis
         * 
         * @return RedisConnectionFactory {@link RedisConnectionFactory}
         */
        @Bean
        @ConfigurationProperties(prefix = "com.mx.santander.commons.redis")
        @ConditionalOnMissingBean(RedisConnectionFactory.class)
        public RedisConnectionFactory jedisConnectionFactory() {
            LOGGER.info("Se inicializa RedisConnectionFactory");
            return new JedisConnectionFactory();
        }

        /**
         * Se crea el cliente de conexion a redis
         * 
         * @param redisConnectionFactory
         *            fabrica de conexiones
         * @return {@link RedisTemplate}
         */
        @Bean
        @ConditionalOnBean(RedisConnectionFactory.class)
        public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            LOGGER.debug("Se inicializa redis Template");
            RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setConnectionFactory(redisConnectionFactory);
            return redisTemplate;
        }
    }

}
