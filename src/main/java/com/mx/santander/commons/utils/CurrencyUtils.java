package com.mx.santander.commons.utils;

import java.text.NumberFormat;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * Clase con utilerias para el formateo de doubles a currency MXN
 * 
 * @author santander
 *
 */
public final class CurrencyUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(CurrencyUtils.class);

    /**
     * Constructor vacio y privado ya que no se permiten instancias del mismo solo
     * acceso a las propiedades estaticas y publicas
     */
    private CurrencyUtils() {

        /**
         * Constructor vacio y privado ya que no se permiten instancias del mismo solo
         * acceso a las propiedades estaticas y publicas
         */
    }

    /**
     * Este metodo recibe un object y lo formatea a currency MXN el objeto de
     * entrada puede ser de cualquier tipo mientras el metodo toString devuelva
     * numeros
     * 
     * @param obj
     *            objeto a covertir a currency
     * @return retorna el string del currency obtenido
     */
    public static String getCurrencyFormat(Object obj) {
        boolean negativo = false;
        Number numero;
        if (!(obj instanceof Number)) {
            numero = Double.parseDouble(obj.toString().replaceAll(",", ""));
        } else {
            numero = (Number) obj;
        }

        if (numero.doubleValue() < 0) {
            LOGGER.debug("La cifra a convertir es negativa");
            negativo = true;
            // convertimos a positivo el valor para dar formato currency
            numero = numero.doubleValue() * -1;
        }
        // se agrega la información del balance obtenido al objeto de salida
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
        String montoPagar = formatter.format(numero);
        // eliminamos el simbolo $
        if (montoPagar.startsWith("$")) {
            montoPagar = montoPagar.substring(1);
        }
        // se valida si el monto es negativo
        if (negativo) {
            return "-" + montoPagar;
        }
        return montoPagar;
    }

    /**
     * Se encarga de tranforma un String en formato currency en Double
     * 
     * @param currencyFormat
     *            string currency
     * @return retorna el valor double del currency informado
     */
    public static Double getDoubleFromCurrencyFormat(String currencyFormat) {
        currencyFormat = currencyFormat.replaceAll(",", "");
        return Double.valueOf(currencyFormat);
    }

}
