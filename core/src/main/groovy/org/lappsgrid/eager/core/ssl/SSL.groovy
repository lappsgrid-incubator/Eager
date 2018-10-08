package org.lappsgrid.eager.core.ssl

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext

/**
 *
 */
class SSL {
    static void enable() {
        TrustManager[] trustAllCerts = new TrustManager[1]
        trustAllCerts[0] = new TrustManager()
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
}
