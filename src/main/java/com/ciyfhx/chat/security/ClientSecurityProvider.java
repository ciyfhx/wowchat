package com.ciyfhx.chat.security;

import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

public class ClientSecurityProvider {

    private static final Logger logger = LoggerFactory.getLogger(ClientSecurityProvider.class);

    private static final String PROTOCOL = "TLSv1.3";
    private static final String CERT_LOCATION = "wowchat-server.cer";

    private static SSLContext sslContext;

    public static SslHandler getSSLHandler() {
        SSLEngine sslEngine = null;
        if (sslContext == null) {
            logger.error("Client SSL context is null");
            throw new IllegalStateException("Unable to initialize Client SSL context");
        } else {
            sslEngine = sslContext.createSSLEngine();
            sslEngine.setUseClientMode(true);
            sslEngine.setNeedClientAuth(false);
            logger.info("Enabled Cipher Suites: " + String.join(",", sslEngine.getEnabledCipherSuites()));
            logger.info("Supported Cipher Suites: " + String.join(",", sslEngine.getSupportedCipherSuites()));
        }
        return new SslHandler(sslEngine);
    }

    public static void initSSLContext() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        logger.info("Initiating SSL context");

        var fac = CertificateFactory.getInstance("X509");
        var is = ClientSecurityProvider.class.getClassLoader().getResourceAsStream(CERT_LOCATION);
        var cert = (X509Certificate) fac.generateCertificate(is);
        logger.info("Certificate is valid from: " + cert.getNotBefore() + " until " + cert.getNotAfter());

        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        keyStore.load(null, null);
        keyStore.setCertificateEntry("server", cert);

        try{
            // Set up trust key manager factory to use our key store
            var trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(keyStore);
            sslContext = SSLContext.getInstance(PROTOCOL);
            sslContext.init(null, trustManagerFactory.getTrustManagers(), null);
        } catch (Exception e) {
            logger.error("Failed to initialize the client-side SSLContext", e);
        }

    }

}
