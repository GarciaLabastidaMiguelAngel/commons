package com.mx.santander.commons.channel.security.aspec;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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

import com.mx.santander.commons.channel.anotation.ValidateHoursOfService;
import com.mx.santander.commons.channel.dao.db.IChannelsDAO;
import com.mx.santander.commons.channel.model.entity.ChannelsEntity;
import com.mx.santander.commons.channel.model.entity.ChannelsEntity.HorarioServicio;
import com.mx.santander.commons.constant.ConstCommons;
import com.mx.santander.commons.constant.ConstErrorCode;
import com.mx.santander.commons.constant.ConstMessageCode;
import com.mx.santander.commons.messages.MessageWithOutTraceException;
import com.mx.santander.commons.utils.RequestUtils;
import com.mx.santander.commons.utils.TimeUtils;

/**
 * Aspecto para validar si la peticion del canal esta dentro de las horas de
 * servicio validas para el canal que realiza la peticion
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Aspect
@Order(Integer.MIN_VALUE + 199)
public class ChannelHoursOfServiceAspect {
    /**
     * logger de la clse
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelHoursOfServiceAspect.class);
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
    public ChannelHoursOfServiceAspect() {
        /**
         * Constructor vacio por default para cumplir con la especificacion y
         * requerimientos de un bean
         * 
         * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
         */
        LOGGER.info("Inicia Aspecto para validar horarios de servicio por canal");
    }

    /**
     * Advice que se encarga de manejar todas las peticiones hechas al
     * microservicios captura todas la peticiones con el point cut donde se
     * establece que deben ser metodos anotados con {@link RequestMapping} y
     * {@link ValidateHoursOfService}
     * 
     * @param pj
     *            point cut
     * @return {@link Object}
     * @throws Throwable
     *             exception
     */
    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) && @annotation(com.mx.santander.commons.channel.anotation.ValidateHoursOfService) && args(..)")
    public Object requestWhitoutRequest(ProceedingJoinPoint pj) throws Throwable {
        LOGGER.debug("Se ejecuta Aspecto para validar si el canal se encuentra en horario de serbicio");
        // validamos si el advice esta dentro los paquetes validos
        if (!pj.getSignature().getDeclaringTypeName().startsWith(packageScan)) {
            return pj.proceed(pj.getArgs());
        }
        // obtenemos el request actual
        HttpServletRequest request = RequestUtils.getCurrentHttpRequest();
        // validamos que tengamos el request actual
        Objects.requireNonNull(request, "Error al obtener el request actual de la peticion.");
        // obtenemos el canal del que proviene la peticion
        String header = request.getHeader(ConstCommons.XCHANNEL);
        LOGGER.debug("header {}:{}", ConstCommons.XCHANNEL, header);
        // buscamos el canal
        Optional<ChannelsEntity> channel = Optional.empty();
        if (!"dev".equalsIgnoreCase(env) || validateMongo) {
            LOGGER.debug("Se consultan los canales en BD");
            List<ChannelsEntity> canales = channelsDao.findAll();
            if (Objects.nonNull(canales)) {
                channel = canales.stream().filter(c -> c.getCanal().equals(header)).findFirst();
            }
        } else {
            // si el entorno es dev creamos el objeto sin conectar a BD
            ChannelsEntity entity = new ChannelsEntity();
            entity.setCanal("SMOV");
            entity.setActivo(true);
            entity.setDescripcion("Super Movil");
            entity.setHorarioServicio(new HorarioServicio());
            entity.getHorarioServicio().setDias(Arrays.asList("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom"));
            entity.getHorarioServicio().setHoraInicio("00:00:00");
            entity.getHorarioServicio().setHoraFin("23:59:59");
            channel = Optional.of(entity);
        }
        HorarioServicio hs = new HorarioServicio();
        // se valida si el canal esta presente
        if (channel.isPresent()) {
            hs = channel.get().getHorarioServicio();
        }
        // validamos si la hora actual esta dentro del horario de servicio
        if (channel.isPresent() && TimeUtils.isNowTimeInRange(hs.getDias(), hs.getHoraInicio(), hs.getHoraFin(),
                new Locale("es", "MX"))) {
            LOGGER.debug(
                    "El canal se encuentra fuera de servicio, horario habilitado, dias:{} , hora inicio:{}, hora fin:{}",
                    hs.getDias(), hs.getHoraInicio(), hs.getHoraFin());
            throw new MessageWithOutTraceException(ConstMessageCode.ACCESO_CANAL_FUERA_SERVICIO,
                    ConstErrorCode.ACCESO_CANAL_FUERA_SERVICIO);
        }
        LOGGER.debug("El canal se encuentra dentro del horario de servicio, dias:{} , hora inicio:{}, hora fin:{}",
                hs.getDias(), hs.getHoraInicio(), hs.getHoraFin());
        // se ejecuta el servicio
        return pj.proceed(pj.getArgs());
    }
}
