package org.lappsgrid.eager.mining.error

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.rabbitmq.pubsub.Publisher
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

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
            po.send(c.BOX_ERROR,"Another Message ${n}")
//            sleep(1000)
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
    void shutdown() {
        PostOffice po = new PostOffice(c.POSTOFFICE)
        po.send(c.BOX_ERROR, 'shutdown')
        po.close()
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
