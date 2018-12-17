package com.mx.santander.commons.autoconfigurations;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de autoconfiguracion para el manejo de los certificados SSL
 * 
 * @author Miguel Angel Garcia Labastida
 *
 */
@Configuration
public class SSLAutoConfiguration implements CommandLineRunner {
    /**
     * logger de la clase
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SSLAutoConfiguration.class);
    /**
     * indica si se valida el SSL
     */
    @Value("${com.mx.santander.commons.validation.ssl.enable:false}")
    private boolean SSLEnable;

    /**
     * configuracion para deshabilitar las validaciones SSL, es configurable ya que
     * se puede desactivar la opcion
     * 
     * @return {@link Void}
     * @throws KeyManagementException
     *             error al crear key store
     * @throws NoSuchAlgorithmException
     *             {@link NoSuchAlgorithmException}
     */
    @Override
    public void run(String... args) throws NoSuchAlgorithmException, KeyManagementException {
        // si la validacion esta habilitada se retorna inmediatamente
        if (SSLEnable) {
            return;
        }
        LOGGER.warn("Las validaciones SSL se encuentran desactivadas");
        final SSLContext sslContext = SSLContext.getInstance("TLS");

        sslContext.init(null, new TrustManager[] { new X509TrustManager() {

            /**
             * Metodo que consulta un cliente de confianza
             * 
             * @param x509Certificates
             *            {@link X509Certificate[]}
             * @param s
             *            {@link String}
             */
            @Override
            public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            /**
             * Metodo que consulta un servidor de confianza
             * 
             * @param x509Certificates
             *            {@link X509Certificate[]}
             * @param s
             *            {@link String}
             */
            @Override
            public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
            }

            /**
             * Metodo que acepta emisoras
             */
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        } }, null);

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            /**
             * Metodo verificador
             * 
             * @param hostname
             *            {@link String}
             * @param session
             *            {@link SSLSession}
             */
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

    }
}
