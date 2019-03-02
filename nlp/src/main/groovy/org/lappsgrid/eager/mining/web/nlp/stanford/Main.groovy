package org.lappsgrid.eager.mining.web.nlp.stanford

import com.codahale.metrics.Meter
import com.codahale.metrics.Timer
import groovy.util.logging.Slf4j
import org.lappsgrid.eager.mining.core.Configuration
import org.lappsgrid.eager.mining.core.jmx.Registry
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.DataContainer
import org.lappsgrid.serialization.Serializer

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 *
 */
@Slf4j('logger')
class Main implements MainMBean {

    /** The number of documents processed. */
    final Meter count = Registry.meter('nlp', 'count')
    /** The number of errors encountered. */
    final Meter errors = Registry.meter('nlp', 'errors')
    /** Processing time for documents. */
    final Timer timer = Registry.timer('nlp', 'timer')

//    public static final Logger logger = LoggerFactory.getLogger(Main)


    final String MAILBOX

    // Since we explicitly create a ThreadPoolExecutor we also need to
    // explicilty create the BlockingQueue used by the pool.
    private BlockingQueue<Runnable> queue
    ThreadPoolExecutor pool

    /** The pipeline of Stanford services to be executed. */
    Pipeline pipeline

    /** System configuration. */
    Configuration config

    /** Object used to block/wait until the queue is closed. */
    Object semaphore

    /** Where outgoing messages are sent. */
    PostOffice post

    /** Where we receive incoming messages. */
    MailBox box

    Main() {
        this(new Configuration())
    }

    Main(Configuration config) {
        this.MAILBOX = config.BOX_NLP_STANFORD
//        this.MAILBOX = "stanford.nlp.pool"

        this.config = config
        this.pipeline = new Pipeline()
        this.semaphore = new Object()
        this.post = new PostOffice(config.POSTOFFICE, "localhost")

        queue = new LinkedBlockingQueue<>()
        int minCores = 2
        int maxCores = 2
        int totalCores = Runtime.getRuntime().availableProcessors()
        if (totalCores <= 2) {
            minCores = maxCores = totalCores
        }
        else {
            maxCores = totalCores // 2
            minCores = maxCores / 2
        }

//        pool = Executors.newWorkStealingPool(maxCores)

        pool = new ThreadPoolExecutor(minCores, maxCores, 30, TimeUnit.SECONDS, queue)
//        ThreadFactory factory = { Runnable r ->
//            println "Creating a new thread"
//            new Thread(r)
//        }
//
//        pool.setThreadFactory(factory)
    }

    void start() {

        logger.info("Staring Standord NLP service.")
        box = new MailBox(config.POSTOFFICE, MAILBOX, config.HOST) {
            @Override
            void recv(String json) {
                Message message
                try {
                    logger.debug("Receieved message. Size: {}", json.length())
                    message = Serializer.parse(json, Message)
                }
                catch (Exception e) {
                    error(e.getMessage())
                    return
                }

                if ('shutdown' == message.command) {
                    logger.info("Received a shutdown message.")
                    stop()
                    return
                }
                if (message.route.size() == 0) {
                    // If there is nowhere to send the result then we have nothing to do.
                    logger.error("Message had no return address")
                    error("NLP tools were sent data but have no route defined.")
                    return
                }

                logger.debug("Staring a worker.")
                Worker worker = new Worker(pipeline, message, post, timer, count, errors)
                pool.execute(worker)
//                queue.add(worker)

//                Timer.Context context = timer.time()
//                DataContainer data
//                try {
//                    data = Serializer.parse(message.body, DataContainer)
//                    // TODO Check the discriminator
//                    data.payload = pipeline.process(data.payload)
//
//                }
//                catch (Exception e) {
//                    logger.error("Unable to process input.", e)
//                    error("NLP tools encountered an exception: " + e.message)
//                    return
//                }
//                finally {
//                    context.close()
//                }
//                logger.debug("Sending result to {}", message.route[0])
//                message.body = data.asJson()
//                post.send(message)
//                count.mark()
            }
        }
    }

    String stats() {
        return String.format("[monitor] [%d/%d/%d] Active: %d\nCompleted: %d\nTask: %d\nisShutdown: %s\nisTerminated: %s",
                this.pool.getPoolSize(),
                this.pool.getCorePoolSize(),
                this.pool.getMaximumPoolSize(),
                this.pool.getActiveCount(),
                this.pool.getCompletedTaskCount(),
                this.pool.getTaskCount(),
                this.pool.isShutdown(),
                this.pool.isTerminated())
    }

    String increase() {
        int cores = Runtime.getRuntime().availableProcessors()
        int size = pool.maximumPoolSize + 1
        pool.maximumPoolSize = size
        if (size < cores) {
            return "OK size is now " + size
        }
        return "Pool size is larger than available processors"
    }

    String decrease() {
        if (pool.maximumPoolSize > 1) {
            pool.maximumPoolSize = pool.maximumPoolSize - 1
            return "OK"
        }
        return "Already at one"
    }

    void stop() {
        logger.info("Stopping the Stanford NLP service")
        pool.shutdown()
        if (!pool.awaitTermination(30, TimeUnit.SECONDS)) {
            pool.shutdownNow()
        }

        if (box) box.close()
        if (post) post.close()
        synchronized (semaphore) {
            semaphore.notifyAll()
        }
    }

    private void error(String message) {
        logger.error(message)
        errors.mark()
//        post.send(config.BOX_ERROR, message)
    }

    static void main(String[] args) {
        Configuration config = new Configuration()
        config.HOST = "localhost"
        config.BOX_NLP_STANFORD = "stanford.nlp.pool"
        Main app = new Main(config)
        Registry.register(app, "org.lappsgrid.eager.mining.nlp.stanford.Main:type=Main")
        Registry.startJmxReporter()
        app.start()

        // Wait forever, or at least until another thread calls notify() or notifyAll() on the semaphore.
        synchronized (app.semaphore) {
            app.semaphore.wait()
        }
        logger.info("Stanford NLP service terminated.")
    }
}
