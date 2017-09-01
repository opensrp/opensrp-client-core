package org.smartregister.ssl;

import android.content.Context;
import android.util.Base64;

import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.smartregister.DristhiConfiguration;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLException;

/**
 * Created by onaio on 01/09/2017.
 */

public class OpensrpSSLHelper {
    private Context context;
    private DristhiConfiguration configuration;

    public OpensrpSSLHelper(Context context_, DristhiConfiguration configuration_) {
        this.context = context_;
        this.configuration = configuration_;
    }

    public SocketFactory getSslSocketFactoryWithOpenSrpCertificate() {

        try {

            String certificateString = "";

            ByteArrayInputStream derInputStream = new ByteArrayInputStream(certificateString.getBytes());
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(derInputStream);
            String alias = cert.getSubjectX500Principal().getName();
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null);
            trustStore.setCertificateEntry(alias, cert);

            SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
            final X509HostnameVerifier oldVerifier = socketFactory.getHostnameVerifier();
            socketFactory.setHostnameVerifier(new AbstractVerifier() {
                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws
                        SSLException {
                    for (String cn : cns) {
                        if (!configuration.shouldVerifyCertificate() || host.equals(cn)) {
                            return;
                        }
                    }
                    oldVerifier.verify(host, cns, subjectAlts);
                }
            });
            return socketFactory;
        } catch (Exception e) {
            throw new AssertionError(e);
        }
    }
}
