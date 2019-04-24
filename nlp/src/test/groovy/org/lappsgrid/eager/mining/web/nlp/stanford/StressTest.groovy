package org.lappsgrid.eager.mining.web.nlp.stanford

import groovy.util.logging.Slf4j
import org.lappsgrid.eager.mining.core.Configuration
import org.lappsgrid.eager.mining.core.jmx.Registry
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MessageBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import java.util.concurrent.atomic.AtomicInteger

/**
 *
 */
@Slf4j("logger")
class StressTest implements StressTestMBean {

    static final String MAILBOX = "stress.test"

    boolean running

    List<Record> data
    int index

    Configuration config
    AtomicInteger outstanding
    int maxOutstanding

    StressTest() {
        logger.info("Creating stress test.")
        this.running = false
        this.data = loadData()
        this.index = 0
        this.config = new Configuration()
        this.config.HOST = "localhost"
        this.config.BOX_NLP_STANFORD = "stanford.nlp.pool"
        this.outstanding = new AtomicInteger(0)
        this.maxOutstanding = 100
    }

    void run() {
        logger.info("Running stress test.")

        MessageBox box = new MessageBox(config.POSTOFFICE, MAILBOX, config.HOST) {
            void recv(Message message) {
                outstanding.decrementAndGet()
                logger.info("Received {}", message.parameters.path)
            }
        }

//        int maxOutstanding = 100

        PostOffice post = new PostOffice(config.POSTOFFICE, config.HOST)
        running = true
        while (running) {
            while (running && outstanding.get() >= maxOutstanding) {
                sleep(500) {
//                    Thread.currentThread().interrupt()
                    return true
                }
            }
            if (running) {
                int count = outstanding.incrementAndGet()
                Record data = next()
                Message message = new Message()
                        .body(data.json)
                        .set("path", data.path)
                        .route("stanford.nlp.pool", MAILBOX)
                post.send(message)
                logger.info("Posted {}. There are {} outstanding tasks", data.path, count)
            }
        }
        logger.info("Shutting down")

        post.close()
        box.close()
    }

    Record next() {
        if (index >= data.size()) {
            index = 0
        }
        return data[index++]
    }

    List<Record> loadData() {
        String path = "/var/corpora/SEMEVAL2017-LIF/scienceie2017_train/train2"
        File directory = new File(path)
        List<Record> records = []
        directory.listFiles().each { File f ->
            records.add(new Record(f))
        }
        return records
    }

    void stop() {
        logger.info("Received stop signal")
        running = false
    }

    String stats() {
        int count = outstanding.get()
        return String.format("There are currently %d outstanding tasks", count)
    }

    void setMaxOutstanding(int max) {
        if (max > 1) {
            logger.info("Set max outstanding to {}", max)
            this.maxOutstanding = max
        }
        else {
            logger.warn("Illegal value for max outstanding {}", max)
        }
    }
    int getMaxOutStanding() {
        return maxOutstanding
    }
    int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors()
    }

    static void main(String[] args) {
        StressTest app = new StressTest()
        Registry.register(app, "org.lappsgrid.eager.mining.nlp.stanford.test:type=Test,name=Stress")
        Registry.startJmxReporter()
        app.run()
    }
}
