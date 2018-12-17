package com.mx.santander.commons.session.security.aspec;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

import java.util.Date;
import java.util.Objects;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mx.santander.commons.constant.ConstCommons;
import com.mx.santander.commons.constant.ConstErrorCode;
import com.mx.santander.commons.constant.ConstMessageCode;
import com.mx.santander.commons.messages.MessageWithOutTraceException;
import com.mx.santander.commons.model.dto.ResponseTO;
import com.mx.santander.commons.model.session.entity.PrincipalUser;
import com.mx.santander.commons.session.anotation.NonValidateSession;
import com.mx.santander.commons.utils.RequestUtils;

/**
 * Aspecto para validar la sesion de lo usuarios si existe una sesion activa
 * llega a la capa de controller de lo contrario no continua la ejecución y
 * manda mensaje informando que la sesión ha termidado, la sesion se debe
 * manejar con redis ya que al ser microservicios puede existir muchas
 * instancias de los mismos y todos deben estar sincronizados con respecto a la
 * informacion en sesion
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Aspect
@Order(Integer.MIN_VALUE + 100)
public class SesionAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(SesionAspect.class);

    /**
     * paquetes a validar
     */
    @Value("${com.mx.santander.commons.packageScan:com.mx.santander}")
    private String packageScan;

    /**
     * Constructor vacio por default para cumplir con la especificacion y
     * requerimientos de un bean
     * 
     * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
     */
    public SesionAspect() {

        /**
         * Constructor vacio por default para cumplir con la especificacion y
         * requerimientos de un bean
         * 
         * @see https://docs.oracle.com/javase/8/docs/technotes/guides/beans/index.html
         */
        LOGGER.info("Inicia Aspecto de sesion");
    }

    /**
     * Advice que se encarga de manejar todas las peticiones hechas al
     * microservicios captura todas la peticiones con el point cut donde se
     * establece que deben ser metodos anotados con {@link RequestMapping} y
     * cualquier numero de argumentos, se ignora si se encuentra la notacion
     * {@link NonValidateSession}
     * 
     * @param pj
     *            point cut
     * @return {@link ResponseTO}
     * @throws Throwable
     *             exception
     */
    @Around("@annotation(org.springframework.web.bind.annotation.RequestMapping) &&  args(..) && !@annotation(com.mx.santander.commons.session.anotation.NonValidateSession)")
    public Object requestWhitoutRequest(ProceedingJoinPoint pj) throws Throwable {
        LOGGER.debug("Se ejecuta aspecto para validar la sesion");
        // validamos si el advice esta dentro los paquetes validos
        if (!pj.getSignature().getDeclaringTypeName().startsWith(packageScan)) {
            return pj.proceed(pj.getArgs());
        }
        // validamos la session
        validateSession();
        HttpSession session = requireNonNull(RequestUtils.getCurrentHttpRequest()).getSession(false);
        Objects.requireNonNull(session, "Error al obtener la sesion de la peticion actual");
        PrincipalUser principal = (PrincipalUser) session.getAttribute(PrincipalUser.ATTRIBUTE_SESSION_NAME);
        Objects.requireNonNull(principal, "No existe informacion del usuario en la sesion");
        LOGGER.debug("La sesion es valida para proceder con la peticion, hora de ultimo acceso:{}",
                principal.getLastAccess());
        principal.setLastAccess(new Date());
        session.setAttribute(PrincipalUser.ATTRIBUTE_SESSION_NAME, principal);
        // si la sesion es valida procede a ejecutar el servicio
        return pj.proceed(pj.getArgs());
    }

    /**
     * metodo para validar si la sesion es valida
     */
    private void validateSession() {
        LOGGER.debug("Entra al metodo para validar si ya existe una session");
        // obtenemos el token de session
        String header = requireNonNull(RequestUtils.getCurrentHttpRequest()).getHeader(ConstCommons.SESSION_HEADER);
        LOGGER.debug("Entra al aspecto para validar la sesion, x-auth-token :{}", header);
        // validamos si existe sesion
        if (isNull(requireNonNull(RequestUtils.getCurrentHttpRequest()).getSession(false))) {
            // si no existe se manda mensaje de sesion expirada
            LOGGER.debug("No existe sesion activa para la peticion , x-auth-token:{}", header);
            throw new MessageWithOutTraceException(ConstMessageCode.SESSION_EXPIRADA, ConstErrorCode.SESSION_EXPIRADA);
        }
    }

}
