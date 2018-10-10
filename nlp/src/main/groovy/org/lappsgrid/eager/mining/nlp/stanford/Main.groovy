package org.lappsgrid.eager.mining.nlp.stanford

import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.DataContainer
import org.lappsgrid.serialization.Serializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 *
 */
class Main {

    public static final Logger logger = LoggerFactory.getLogger(Main)

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
                Message message = Serializer.parse(json, Message)
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

                logger.debug("Processing the payload.")
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
                logger.info("Sending result to {}", message.route[0])
                message.body = data.asJson()
                post.send(message)

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
        post.send(config.BOX_ERROR, message)
    }

    public static void main(String[] args) {
        Main app = new Main()
        app.start()

        // Wait forever, or at least until another thread notifies() us.
        synchronized (app.semaphore) {
            app.semaphore.wait()
        }
        logger.info("Stanford NLP service terminated.")
    }
}
