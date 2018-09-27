package org.lappsgrid.eager.mining.error

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.rabbitmq.pubsub.Publisher
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 *
 */
@Ignore
class IntegrationTest {

    @Test
    void run() {
        // Send some error messages to the error logger.
        PostOffice po = new PostOffice('eager.postoffice')
//        po.send('error', '---')
        50.times { n ->
            po.send('error',"Message ${50 - n}")
//            sleep(1000)
        }
//        println "Sleeping"
//        sleep(5000)
//
//        println "Sending shutdown message"
//        Publisher pub = new Publisher('eager.broadcast')
//        pub.publish("shutdown")
//
//        sleep(2000)
//        println "Done"
    }


    @Test
    void message() {
        PostOffice po = new PostOffice('eager.postoffice')
        try {
            po.send('error', "Message one")
        }
        catch (Exception e) {
            e.printStackTrace()
        }
    }

    @Test
    void ping() {
        Publisher pub = new Publisher('eager.broadcast')
        pub.publish('ping')
        pub.publish('ping')
    }

    @Test
    void shutdown() {
        Publisher pub = new Publisher('eager.broadcast')
        pub.publish('shutdown')
    }
}
