package org.lappsgrid.eager.mining.test

import groovy.cli.picocli.CliBuilder
import groovy.json.JsonOutput
import groovy.json.JsonParser
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue
import org.lappsgrid.eager.rabbitmq.tasks.Worker

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 *
 */
class Main {

    /*
    void query() {
        enable_ssl()
        String limit="10"
        String inclusive="true"
        String terms=URLEncoder.encode("what happened during earth's dark age", 'UTF-8')
        String url = "https://geodeepdive.org/api/snippets?limit=$limit&inclusive=true&term=$terms&clean"
        println url
//        String json = new URL(url).text
        Map data = new JsonSlurper().parse(new URL(url))
        data.success.data.each { record ->
            println "$record.hits $record.title"
            record.highlight.each { println it }
        }

    }

    void parse() {
//        File file = new File('/tmp/journals.json')
        InputStream stream = this.class.getResourceAsStream("/journals.json")
        Map data = new JsonSlurper().parse(stream)
        int count = 0
        int total = 0
        data.success.data.findAll { it.publisher == 'PubMed Central' }.each { record ->
            ++count
            total += record.articles
            println "${record.journal} : ${record.articles}"
        }
        println "Count: $count"
        println "Total: $total"
//        Set publishers = [] as HashSet
//        data.success.data.each { record -> publishers.add(record.publisher) }
//        publishers.sort().each { println it }
    }

    void download() {
        enable_ssl()
        URL url = new URL("https://geodeepdive.org/api/journals?all")
//        InputStream input = url.openStream()
//        String json = new JsonSlurper().setType(JsonParserType.LAX).parse(input)
        new File('src/main/resources/journals.json').text = url.text // JsonOutput.prettyPrint(json)
    }

    void enable_ssl() {
        TrustManager[] trustAllCerts = new TrustManager[1]
        trustAllCerts[0] = new TrustManager()
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    static void main(String[] args) {
        new Main().query()
    }
    */
}

//class TrustManager implements X509TrustManager {
//    public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null;  }
//    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
//    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
//}
