package org.lappsgrid.eager.mining.error

import org.junit.Ignore
import org.junit.Test
import static org.junit.Assert.*

import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.rabbitmq.pubsub.Publisher
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *
 */
@Ignore
class IntegrationTest {

    Configuration c = new Configuration()

    @Test
    void run() {
        // Send some error messages to the error logger.
        PostOffice po = new PostOffice(c.POSTOFFICE)
        10.times { n ->
            println "Sending $n"
            po.send(c.BOX_ERROR,"Even moar messages - ${n}")
            sleep(500)
        }
        po.close()
    }


    @Test
    void ping() {
        Publisher pub = new Publisher('eager.broadcast')
        pub.publish('ping')
        pub.publish('ping')
    }

    @Test
    void key() {
        String mbox = 'error-integration-return'
        String key
        CountDownLatch latch = new CountDownLatch(1)
        MailBox box = new MailBox(c.POSTOFFICE, mbox) {
            @Override
            void recv(String message) {
                key = message
                latch.countDown()
            }
        }

        PostOffice po = new PostOffice(c.POSTOFFICE)
        po.send(c.BOX_ERROR, "key $mbox")
        po.close()
        if (!latch.await(4, TimeUnit.SECONDS)) {
            fail "No response from the error service"
        }
        if (key == null) {
            fail 'No key was returned'
        }
        //box.close()
        println "Received key $key"
    }

    @Test
    void shutdown() {
        String key = '20e82581-5521-4ea7-99f5-a090afaf7c42'
        PostOffice po = new PostOffice(c.POSTOFFICE)
        po.send(c.BOX_ERROR, "shutdown $key")
        po.close()
    }

    @Test
    void collect() {
        CountDownLatch latch = new CountDownLatch(1)

        String mbox = 'error-collector'
        MailBox box = new MailBox(c.POSTOFFICE, mbox) {
            @Override
            void recv(String message) {
                println message
                latch.countDown()
            }
        }

        PostOffice po = new PostOffice(c.POSTOFFICE)
        po.send(c.BOX_ERROR, "collect $mbox")
        po.close()
        if (!latch.await(5, TimeUnit.SECONDS)) {
            fail "No response from the error service!"
        }
        println "Done."
    }

    @Test
    void drain() {
        Configuration c = new Configuration()
        MailBox box = new MailBox(c.POSTOFFICE, c.BOX_ERROR) {

            @Override
            void recv(String message) {
                println message
            }
        }

        Object lock = new Object()
        synchronized (lock) {
            lock.wait()
        }
//        sleep(5000)
    }
}
