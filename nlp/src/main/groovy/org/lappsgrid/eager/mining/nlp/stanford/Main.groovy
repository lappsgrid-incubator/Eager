package org.lappsgrid.eager.mining.nlp.stanford

import com.codahale.metrics.Meter
import com.codahale.metrics.Timer
import groovy.util.logging.Slf4j
import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.core.jmx.Registry
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.DataContainer
import org.lappsgrid.serialization.Serializer

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
        this.config = config
        this.pipeline = new Pipeline()
        this.semaphore = new Object()
        this.post = new PostOffice(config.POSTOFFICE)
    }

    void start() {

        logger.info("Staring Standord NLP service.")
        box = new MailBox(config.POSTOFFICE, config.BOX_NLP_STANFORD) {
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

                logger.trace("Processing the payload.")
                Timer.Context context = timer.time()
                DataContainer data
                try {
                    data = Serializer.parse(message.body, DataContainer)
                    // TODO Check the discriminator
                    data.payload = pipeline.process(data.payload)

                }
                catch (Exception e) {
                    logger.error("Unable to process input.", e)
                    error("NLP tools encountered an exception: " + e.message)
                    return
                }
                finally {
                    context.close()
                }
                logger.debug("Sending result to {}", message.route[0])
                message.body = data.asJson()
                post.send(message)
                count.mark()
            }
        }
    }

    void stop() {
        logger.info("Stopping the Stanford NLP service")
        if (box) box.close()
        if (post) post.close()
        synchronized (semaphore) {
            semaphore.notifyAll()
        }
    }

    private void error(String message) {
        logger.error(message)
        errors.mark()
        post.send(config.BOX_ERROR, message)
    }

    public static void main(String[] args) {
        Main app = new Main()
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
