package org.lappsgrid.eager.mining.section

import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.eager.mining.api.Sink
import org.lappsgrid.eager.mining.api.Source
import org.lappsgrid.eager.mining.api.Worker
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch

/**
 *
 */
class RemoteDocumentProvider extends Source {
    static final String MAILBOX = "document-provider"
    private static final Packet END = new Packet()

    private Configuration conf

    /** The queue used to hold items returned by the produce() method. */
    BlockingQueue<Packet> queue

    /** The list of all PMC files. */
    List<String> index

    RemoteDocumentProvider(BlockingQueue<String> output) {
        super("RemoteDocumentProvider", output)
        Thread.start { init() }
    }

    RemoteDocumentProvider(Sink sink, BlockingQueue<String> output) {
        super("RemoteDocumentProvider", sink, output)
        Thread.start {
            init()
        }
    }

    Object produce() {
        Packet next = queue.take()
        if (END == next) {
            println "RemoteDocumentProvider has run out of data."
            return Worker.DONE
        }
        println "RemoteDocumentProvider.produce"
        return next
    }

    void init() {

        Random random = new Random()
        random.seed = System.currentTimeMillis()

        conf = new Configuration()
        queue = new ArrayBlockingQueue<>(1024)
        index = new ArrayList<>()

        InputStream stream = this.class.getResourceAsStream("/pmc-index.txt")
        stream.eachLine { index.add(it) }

        int count = 1000

        CountDownLatch latch = new CountDownLatch(count)
        MailBox box = new MailBox(conf.POSTOFFICE, MAILBOX) {
            void recv(String json) {
                Message message = Serializer.parse(json, Message)
                if (message.command == 'loaded') {
                    //println message.body
                    queue.put(message.body)
                }
                latch.countDown()
                if (latch.count == 0) {
                    println "RemoteDocumentProvider.recv: adding END to the queue"
                    queue.put(END)
                }
            }
        }

        Thread.start {
            PostOffice po = new PostOffice(conf.POSTOFFICE)
            count.times { i ->
                int offset = random.nextInt(index.size())
                String path = index[offset]
                println "RemoteDocumentProvider.init: fetching $path"
                Message message = new Message()
                    .command("load")
                    .body(path)
                    .route(conf.BOX_LOAD, MAILBOX)
                po.send(message)
            }
            println "RemoteDocumentProvider.init: closing the post office."
            po.close()
        }

        if (!latch.await()) {
            println "There was an error waiting for the count down latch."
        }
        box.close()
    }

}
