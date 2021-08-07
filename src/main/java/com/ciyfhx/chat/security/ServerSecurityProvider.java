package com.ciyfhx.chat.security;

import io.netty.handler.ssl.SslHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class ServerSecurityProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServerSecurityProvider.class);

    private static final String PROTOCOL = "TLSv1.3";
    private static final String KEYSTORE_LOCATION = "wowchat-server.jks";
    private static final String KEYSTORE_PASSWORD = "wowchat";
    private static final String KEYSTORE_TYPE = "JKS";
    private static final String CERT_PASSWORD = "wowchat";

    private static SSLContext serverSSLContext;

    public static SslHandler getSSLHandler() {
        SSLEngine sslEngine = null;
        if (serverSSLContext == null) {
            logger.error("Server SSL context is null");
            throw new IllegalStateException("Unable to initialize Server SSL context");
        } else {
            sslEngine = serverSSLContext.createSSLEngine();
            sslEngine.setUseClientMode(false);
            sslEngine.setNeedClientAuth(false);
            sslEngine.setEnabledProtocols(new String[]{PROTOCOL});
            sslEngine.setEnabledCipherSuites(new String[]{"TLS_AES_256_GCM_SHA384", "TLS_CHACHA20_POLY1305_SHA256"});
            logger.info("Enabled Cipher Suites: " + String.join(",", sslEngine.getEnabledCipherSuites()));
            logger.info("Supported Cipher Suites: " + String.join(",", sslEngine.getSupportedCipherSuites()));
        }
        return new SslHandler(sslEngine);
    }

    public static void initSSLContext() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        logger.info("Initiating SSL context");
        KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
        ks.load(ServerSecurityProvider.class.getClassLoader().getResourceAsStream(KEYSTORE_LOCATION),
                KEYSTORE_PASSWORD.toCharArray());

        try {
            // Set up key manager factory to use our key store
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, CERT_PASSWORD.toCharArray());
            KeyManager[] keyManagers = kmf.getKeyManagers();

            serverSSLContext = SSLContext.getInstance(PROTOCOL);
            serverSSLContext.init(keyManagers, null, null);

        } catch (Exception e) {
            logger.error("Failed to initialize the server-side SSLContext", e);
        }


    }

}
