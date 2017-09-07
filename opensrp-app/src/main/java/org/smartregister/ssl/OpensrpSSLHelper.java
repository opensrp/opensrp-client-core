package org.smartregister.ssl;

import android.util.Log;

import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.AbstractVerifier;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.smartregister.DristhiConfiguration;

import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateExpiredException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLException;

/**
 * Created by onaio on 01/09/2017.
 */

public class OpensrpSSLHelper {
    private static DristhiConfiguration configuration;
    private static String TAG = OpensrpSSLHelper.class.getCanonicalName();

    public OpensrpSSLHelper(DristhiConfiguration configuration_) {
        this.configuration = configuration_;
    }

    public SocketFactory getSslSocketFactoryWithOpenSrpCertificate(final String url) throws Exception {
        try {

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            X509Certificate cert = getSSLServerCertificate(url);
            String alias = cert.getSubjectX500Principal().getName();
            trustStore.load(null);
            trustStore.setCertificateEntry(alias, cert);

            SSLSocketFactory socketFactory = new SSLSocketFactory(trustStore);
            final X509HostnameVerifier oldVerifier = socketFactory.getHostnameVerifier();
            socketFactory.setHostnameVerifier(new AbstractVerifier() {
                @Override
                public void verify(String host, String[] cns, String[] subjectAlts) throws
                        SSLException {
                    for (String cn : cns) {
                        if (false || host.equals(cn)) {
                            return;
                        }
                    }
                    oldVerifier.verify(host, cns, subjectAlts);
                }
            });
            return socketFactory;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

    }

    public X509Certificate getSSLServerCertificate(String url) throws Exception {
        List<X509Certificate> certificates = getValidSSLCertificates(url);
        URL destinationURL = new URL(url);
        for (X509Certificate certificate : certificates) {
            X500Name x500name = new X500Name(certificate.getSubjectX500Principal().getName());
            RDN cn = x500name.getRDNs(BCStyle.CN)[0];

            String principal = IETFUtils.valueToString(cn.getFirst().getValue());
            if (principal.equalsIgnoreCase(destinationURL.getHost())) {
                return certificate;
            }
        }
        return null;
    }

    public static List<X509Certificate> getValidSSLCertificates(String aURL) throws Exception {

        URL destinationURL = new URL(aURL);
        List<X509Certificate> certificates = new ArrayList<>();
        HttpsURLConnection connection = (HttpsURLConnection) destinationURL
                .openConnection();
        connection.connect();
        Certificate[] certs = connection.getServerCertificates();
        connection.disconnect();
        for (Certificate cert : certs) {
            if (cert instanceof X509Certificate) {
                X509Certificate x509Certificate = (X509Certificate) cert;
                try {
                    x509Certificate.checkValidity();
                    Log.d(TAG, "Certificate " + x509Certificate.getSubjectDN().getName() + " is Active");
                    certificates.add(x509Certificate);
                } catch (CertificateExpiredException cee) {
                    Log.e(TAG, "Certificate " + x509Certificate.getSubjectDN().getName() + " is expired");
                }
            }
        }
        return certificates;
    }
}
