package org.lappsgrid.eager.mining.io

import org.lappsgrid.eager.mining.api.Sink
import org.lappsgrid.eager.mining.api.Source
import org.lappsgrid.eager.mining.api.Worker
import org.lappsgrid.eager.mining.core.Configuration
import org.lappsgrid.eager.mining.core.json.Serializer
import org.lappsgrid.eager.mining.index.PMCIndex
import org.lappsgrid.eager.mining.section.Packet
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.CountDownLatch

/**
 * The IndexLister does not actually "list" a directory, but instead delivers files from
 * the document retrieval service.
 */
class IndexLister extends Source implements Lister {
    static final String MAILBOX = "eager.document.provider"

    private static final Packet END = new Packet()
    private Configuration conf

    /** The queue used to hold items returned by the produce() method. */
    BlockingQueue<Packet> queue

    /** The list of all PMC files. */
    PMCIndex index

    /**
     * If sampleSize < 0 then request all documents in the index. Otherwise just
     * return this number of documents.
     */
    int sampleSize = -1

    /**
     * Are we sampling document at random or retrieving in sequence.
     */
    boolean randomSample = false

    /**
     * PostOffice to send requests to.
     */
    PostOffice po

    /**
     * RNG used to generate random samples.
     */
    Random random

    /**
     * Index counter used when retrieving documents in sequential order.
     */
    int currentIndex

    private boolean terminated

    IndexLister(BlockingQueue<String> output) {
        super("RemoteDocumentProvider", output)
        queue = new ArrayBlockingQueue<>(1024)
        terminated = false
        Thread.start { init() }
    }

    IndexLister(Sink sink, BlockingQueue<String> output, int size) {
        super("RemoteDocumentProvider", sink, output)
        queue = new ArrayBlockingQueue<>(1024)
        sampleSize = size
        terminated = false
        Thread.start {
            init()
        }
    }

    Object produce() {
        if (terminated) {
            return Worker.DONE
        }

        Packet next = queue.take()
        if (END == next) {
            //logger.info("RemoteDocumentProvider has run out of data.")
            return Worker.DONE
        }
        return next
    }

    void terminate() {
        terminated = true
    }

    void init() {
        //logger.debug("Initializing the input queue")
        random = new Random()
        random.seed = System.currentTimeMillis()

        conf = new Configuration()
        index = new PMCIndex()
        currentIndex = 0

        int count
        if (randomSample) {
            if (sampleSize < 1) {
                count = 100
            }
        }
        else if (sampleSize < 0) {
            count = index.size()
            randomSample = false
        }
        else {
            count = sampleSize
        }
        println "Sample size: $count"
        sink.total = count

        //logger.debug("Requesting {} documents", count)
        CountDownLatch latch = new CountDownLatch(count)
        MailBox box = new MailBox(conf.POSTOFFICE, MAILBOX) {
            void recv(String json) {
                println "Recevied ${json.size()} characters"
                Message message = Serializer.parse(json, Message)
                if (message.command == 'loaded') {
                    //println message.body
                    Packet packet = new Packet()
                        .xml(message.body)
                        .path(message.parameters.path)
                    //logger.debug("Received {}", packet.path)
                    println "Received ${packet.path}"
                    queue.put(packet)
                }
                else {
                    println "Recieved invalid message: ${message.command} : ${message.body}"
                }
                latch.countDown()
                if (latch.count == 0) {
                    //logger.debug("Adding END to the queue")
                    queue.put(END)
                    po.close()
                }
                else {
                    requestNextDocument()
                }
            }
        }

        po = new PostOffice(conf.POSTOFFICE)
        requestNextDocument()

//        count.times { i ->
//            int offset = random.nextInt(index.size())
//            String path = index[offset]
//            //logger.trace("Fetching {}", path)
//            Message message = new Message()
//                .command("load")
//                .body(path)
//                .route(conf.BOX_LOAD, MAILBOX)
//            po.send(message)
//        }
//        //logger.debug("closing the post office.")
//        po.close()

        try {
            if (!latch.await()) {
                //logger.error("There was an error waiting for the count down latch.")
            }
        }
        catch (InterruptedException e) {
            // If we were interrupted we need to pass that fact along to any other threads
            // that are waiting on us.
            //logger.info("Latch was interrupted.")
            Thread.currentThread().interrupt()
        }
//        box.close()
    }

    void requestNextDocument() {
        int offset
        if (randomSample) {
            offset = random.nextInt(index.size())
        }
        else {
            offset = currentIndex++
        }
        String path = index[offset]
        //logger.trace("Fetching {}", path)
        println "Fetching $path"
        Message message = new Message()
                .command("load")
                .body(path)
                .route(conf.BOX_LOAD, MAILBOX)
        println Serializer.toPrettyJson(message)
        po.send(message)
        println "Message sent."
    }
}
