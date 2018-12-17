package com.mx.santander.commons.channel.security.aspec;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mx.santander.commons.channel.dao.db.IChannelsDAO;
import com.mx.santander.commons.channel.model.entity.ChannelsEntity;
import com.mx.santander.commons.constant.ConstCommons;
import com.mx.santander.commons.constant.ConstErrorCode;
import com.mx.santander.commons.constant.ConstMessageCode;
import com.mx.santander.commons.messages.MessageWithOutTraceException;
import com.mx.santander.commons.model.dto.ResponseTO;
import com.mx.santander.commons.utils.RequestUtils;

/**
 * Aspecto para validar si el canal tiene acceso a consumir servicios de la API
 * de Go Pay, el canal debe venir informado por el header x-channel se valida en
 * una colecion de mongo si el canal tiene acceso, si el canal no tiene acceso
 * no se le da acceso al servicio y se le informa un error generico para que
 * valide el canal informado en el siguiente formato establecido
 * 
 * <pre>
 * {
 * "codigoDeOperacion": 0, "mensaje":{ "titulo":"Atencion", "texto" :"Operacion
 * Exitosa", "codigoError":"ERR999", }, "data":{} }
 * </pre>
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Aspect
@Order(Integer.MIN_VALUE + 200)
public class ChannelAccessAspect {
    /**
     * logger de la clse
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelAccessAspect.class);
    /**
     * Dao de canales
     */
    @Autowired
    private IChannelsDAO channelsDao;
    /**
     * paquetes a escanear
     */
    @Value("${com.mx.santander.commons.packageScan:com.mx.santander}")
    private String packageScan;
    /**
     * ambiente de ejecucion
     */
    @Value("${spring.profiles.active:dev}")
    private String env;
    /**
     * validar si se activa validacion con mongo
     */
    @Value("${com.mx.santander.commons.channel.access.validation.mongo.enable:false}")
    private boolean validateMongo;

    /**
     * Constructor vacio por default para cumplir con la especificacion y
     * requerimientos de un bean
     * 
     * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
     */
    public ChannelAccessAspect() {
        /**
         * Constructor vacio por default para cumplir con la especificacion y
         * requerimientos de un bean
         * 
         * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
         */
        LOGGER.info("Inicia Aspecto para validar el acceso de los canales a los servicios");
    }

    /**
     * Advice que se encarga de manejar todas las peticiones hechas al
     * microservicios captura todas la peticiones con el point cut donde se
     * establece que deben ser metodos anotados con {@link RequestMapping} y
     * cualquier numero de argumentos
     * 
     * @param pj
     *            point cut
     * @return {@link ResponseTO}
     * @throws Throwable
     *             exception
     */
    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) &&  args(..) && !@annotation(com.mx.santander.commons.channel.anotation.NoValidateChannelOfService)")
    public Object requestWhitoutRequest(ProceedingJoinPoint pj) throws Throwable {
        LOGGER.debug("Se ejecuta Aspecto para validar el canal que hace la peticion");
        // validamos si el advice esta dentro los paquetes validos
        if (!pj.getSignature().getDeclaringTypeName().startsWith(packageScan)) {
            return pj.proceed(pj.getArgs());
        }
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        Objects.requireNonNull(request, "Error al obtener el request actual de la peticion.");
        String header = request.getHeader(ConstCommons.XCHANNEL);
        LOGGER.debug("header {}:{}", ConstCommons.XCHANNEL, header);
        // buscamos el canal
        Optional<ChannelsEntity> channel;
        if (!"dev".equalsIgnoreCase(env) || validateMongo) {
            LOGGER.debug("Se consultan los canales en BD");
            List<ChannelsEntity> canales = channelsDao.findAll();
            if (Objects.nonNull(canales)) {
                channel = canales.stream().filter(c -> c.getCanal().equals(header)).findFirst();
            } else {
                LOGGER.error("No existen canales registrados en BD para validar el acceso");
                channel = Optional.empty();
            }
        } else {
            ChannelsEntity entity = new ChannelsEntity();
            entity.setCanal("SMOV");
            entity.setActivo(true);
            entity.setDescripcion("Super Movil");
            channel = Optional.of(entity);
        }

        // validamos si tiene acceso
        if (!channel.isPresent() || !channel.get().getCanal().equals(header)) {
            LOGGER.error("Acceso denegado header {}:{}", ConstCommons.XCHANNEL, header);
            throw new MessageWithOutTraceException(ConstMessageCode.ACCESO_DENEGADO_CANAL,
                    ConstErrorCode.ACCESO_DENEGADO_CANAL);
        }
        if (!channel.get().isActivo()) {
            LOGGER.warn("El canal tiene desactivado el acceso, se encuentra fuera de servicio");
            throw new MessageWithOutTraceException(ConstMessageCode.ACCESO_CANAL_FUERA_SERVICIO,
                    ConstErrorCode.ACCESO_CANAL_FUERA_SERVICIO);
        }
        LOGGER.debug("Se permite el acceso al canal:{} de {}", channel.get().getCanal(), channel.get().getNombre());
        return pj.proceed(pj.getArgs());
    }
}
