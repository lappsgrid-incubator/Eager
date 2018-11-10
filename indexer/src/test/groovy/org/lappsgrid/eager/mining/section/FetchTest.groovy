package org.lappsgrid.eager.mining.section

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.MessageBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import java.util.concurrent.CountDownLatch

/**
 *
 */
@Ignore
class FetchTest {

    @Test
    void fetch() {
        //https://www.ncbi.nlm.nih.gov/pmc/articles/PMC5292547/?report=classic

        String path = "/var/data/pmc/xml/non/Edinb_Med_J/PMC5292547.nxml"
        String mailbox = "fetch.test"
        Configuration conf = new Configuration()

        CountDownLatch latch = new CountDownLatch(1)
        MessageBox box = new MessageBox(conf.POSTOFFICE, mailbox) {
            @Override
            void recv(Message message) {
                println message.command
                println message.route.join(",")
                println message.body
                println()
                latch.countDown()
            }
        }

        PostOffice po = new PostOffice(conf.POSTOFFICE)
        Message message = new Message()
                .body(path)
                .command("load")
                .route(conf.BOX_LOAD, mailbox)
        po.send(message)
        if (!latch.await()) {
            println "There was a problem waiting for the latch."
        }
        po.close()
        println "Done"
    }
}
