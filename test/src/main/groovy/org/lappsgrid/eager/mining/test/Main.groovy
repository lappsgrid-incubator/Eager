package org.lappsgrid.eager.mining.test

import groovy.cli.picocli.CliBuilder
import groovy.json.JsonOutput
import groovy.json.JsonParser
import groovy.json.JsonParserType
import groovy.json.JsonSlurper
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue
import org.lappsgrid.eager.rabbitmq.tasks.Worker
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.security.auth.login.AppConfigurationEntry

/**
 *
 */
class Main {

    static final Configuration c = new Configuration()
    void error(String message) {
        PostOffice po = new PostOffice()
    }

    static void main(String[] args) {
        CliBuilder cli = new CliBuilder('messanger')

    }
}

