package org.lappsgrid.eager.mining

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.mining.core.Configuration
import org.lappsgrid.eager.mining.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import java.util.concurrent.CountDownLatch

/**
 *
 */
@Ignore
class Experimental {

    @Test
    void test() {
        String mailbox = 'loader.test'
        String path = '/var/data/pmc/xml/com/Inflamm_Regen/PMC5828134.nxml'

        Configuration c = new Configuration()
        CountDownLatch latch = new CountDownLatch(1)
        MailBox box = new MailBox(c.POSTOFFICE, mailbox) {
            @Override
            void recv(String message) {
                println message
                latch.countDown()
            }
        }

        Message message = new Message()
                .command("load")
                .body(path)
                .route(c.BOX_LOAD, mailbox)

        PostOffice po = new PostOffice(c.POSTOFFICE)
        po.send(message)
        latch.await()
        po.close()
        box.close()
        println "Done."
    }
}
