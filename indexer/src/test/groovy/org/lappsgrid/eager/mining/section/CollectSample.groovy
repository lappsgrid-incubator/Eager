package org.lappsgrid.eager.mining.section

import org.lappsgrid.eager.mining.core.Configuration
import org.lappsgrid.eager.mining.index.PMCIndex
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MessageBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import java.util.concurrent.CountDownLatch

/**
 *
 */
class CollectSample implements Runnable {

    static final String MAILBOX = "sample.collector"

    Configuration conf

    void run() {
        conf = new Configuration()

        Random random = new Random()
        random.seed = System.currentTimeMillis()

        File directory = new File("src/test/resources")
        if (!directory.exists() || !directory.isDirectory()) {
            println "Invalid target directory: ${directory.path}"
            return
        }
        int count = 50
        CountDownLatch latch = new CountDownLatch(count)
        MessageBox box = new MessageBox(conf.POSTOFFICE, MAILBOX) {
            @Override
            void recv(Message message) {
                if (message.command == 'loaded') {
                    if (message.parameters.path) {
                        File file = new File(message.parameters.path)
                        File destination = new File(directory, file.name)
                        destination.text = message.body
                        println "Wrote ${destination.path}"
                    }
                    else {
                        println "No path was returned with the document..."
                    }
                }
                latch.countDown()
            }
        }

        PMCIndex index = new PMCIndex()
        PostOffice po = new PostOffice(conf.POSTOFFICE)
        count.times {
            int n = random.nextInt(index.size())
            String path = index[n]
            Message message = new Message()
                    .body(path)
                    .command("load")
                    .route(conf.BOX_LOAD, MAILBOX)
            po.send(message)
        }
        po.close()

        if (!latch.await()) {
            println "There was an error waiting for the latch."
        }
        box.close()
        println "Done."
    }

    static void main(String[] args) {
        new CollectSample().run()
    }
}
