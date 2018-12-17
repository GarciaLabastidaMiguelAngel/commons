package com.mx.santander.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 * Clase con utilidades para horas y tiempos
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
public final class TimeUtils {
    /**
     * Constructor privado para evitar generar instacias
     */
    private TimeUtils() {

    }

    /**
     * Metodo para validar si la hora actual del sistema se encuentra en el rango de
     * dias y horas especificadas
     * 
     * @param dias
     *            dias validos
     * @param horaInicio
     *            hora de inicio debe tener el formato HH:mm:ss, con un formato de
     *            hora de 0-23
     * @param horaFin
     *            hora de fin debe tener el formato HH:mm:ss, con un formato de hora
     *            de 0-23
     * @param locale
     *            {@link Locale}
     * @return retorna true si la hora actual del sistema se encuentra dentro del
     *         rago de dias y horas validas, de lo contrario retorna false
     * @throws ParseException
     *             error si las horas no cumplen con el formato
     */
    public static boolean isNowTimeInRange(List<String> dias, String horaInicio, String horaFin, Locale locale)
            throws ParseException {
        Objects.requireNonNull(dias, "dias no puede ser nulo");
        Objects.requireNonNull(horaInicio, "horaInicio no puede ser nulo");
        Objects.requireNonNull(horaFin, "horaFin no puede ser nulo");
        Objects.requireNonNull(locale, "locale no puede ser nulo");
        // obtenemos la hora actual
        Date now = new Date();
        // damos el formato para obtener el dia
        SimpleDateFormat day = new SimpleDateFormat("EEE", locale);
        // fomarto para obtener la fecha
        SimpleDateFormat date = new SimpleDateFormat("yyyy-mm-dd");
        // obtenemos el dia de la semana
        String dayString = day.format(now);
        // capitalizamos el dia
        dayString = StringUtils.capitalize(dayString);
        // validamos si el dia es valido en la lista de dias
        if (!dias.contains(dayString)) {
            return false;
        }
        SimpleDateFormat formatoHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", locale);
        // hora inicio
        Date hi = formatoHora.parse(date.format(now) + " " + horaInicio);
        // hora fin
        Date hf = formatoHora.parse(date.format(now) + " " + horaFin);
        // validamos si la hora actual se encuentra entre la hora inicio y la hora fin
        return now.after(hi) && now.before(hf);
    }
}
