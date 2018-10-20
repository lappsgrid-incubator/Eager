package org.lappsgrid.eager.mining.error

import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.rabbitmq.pubsub.Subscriber
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicLong

/**
 * Logs messages to 'error' on the 'eager.postoffice' exchange.
 */
class MessageHandler {
    // TODO this should be parsed from the lof4j2.properties file.
    private static final String LOG_FILE = "/tmp/error.log"

    private static Logger logger = LoggerFactory.getLogger(MessageHandler)
    private Logger errorLogger = LoggerFactory.getLogger("error-logger")

    // RabbitMQ configuration.
    Configuration configuration

    String shutdownKey

    // The mail box log messages will be sent to.
    MailBox box

    // Semaphore used to signal when the thread should terminate.
    private Object semaphore

    AtomicLong counter

    MessageHandler() {
        this(new Configuration())
    }

    MessageHandler(Configuration config) {
        this.configuration = config
        this.semaphore = new Object()
        this.counter = new AtomicLong()
        this.shutdownKey = UUID.randomUUID().toString()
    }

    /**
     * Start the message queue listeners.
     */
    void start() {
        box = new MailBox(configuration.POSTOFFICE, configuration.BOX_ERROR) {
            @Override
            void recv(String message) {
                counter.incrementAndGet()
                if (message.startsWith('shutdown')) {
                    String key = parseCommand(message)
                    if (key == null) {
                        logger.warn("Invalid shutdown message received: {}", message)
                        return
                    }
                    if (key != shutdownKey) {
                        logger.warn("Shutdown with invalid key.")
                        logger.warn("Expected: {} Received: {}", shutdownKey, key)
                        return
                    }
                    logger.info('Received a shutdown message')
                    synchronized (semaphore) {
                        semaphore.notifyAll()
                    }
                }
                else if (message.startsWith('key')) {
                    String address = parseCommand(message)
                    if (address == null) {
                        logger.warn("Invalid key command")
                        return
                    }
                    logger.info("Sending shutdown key to {}", address)
                    PostOffice po = new PostOffice(configuration.POSTOFFICE)
                    po.send(address, shutdownKey)
                    po.close()
                }
                else if (message.startsWith('collect')) {
                    logger.info("Received a collect messag")
//                    String[] parts = message.split("\\s+")
                    String address = parseCommand(message)
                    if (address != null) {
                        File logFile = new File(LOG_FILE)
                        String response
                        if (!logFile.exists()) {
                            response = "Log file not found."
                        }
                        else {
                            response = logFile.text
                        }
                        send(address, response)
                    }
                    else {
                        logger.error("Received an invalid collect message: {}", message)
                    }
                }
                else {
                    logger.info("Logging {}", message)
                    errorLogger.info(message)
                }
            }
        }
        logger.info("Handler waiting for messages")
    }

    long count() {
        return counter.get()
    }

    void send(String address, String message) {
        PostOffice po = new PostOffice(configuration.POSTOFFICE)
        po.send(address, message)
        po.close()
    }

    void close() {
//        broadcaster.close()
        box.close()
        logger.info('Closed all connections.')
    }

    private String parseCommand(String input) {
        String[] parts = input.split("\\s+")
        if (parts.length != 2) {
            return null
        }
        return parts[1]
    }

    static void main(String[] args) {
        logger.info("Staring MessageHandler")
        MessageHandler handler = new MessageHandler()
        handler.start()
        logger.info("MessageHandler started.")

        // Wait forever until some other thread wakes us up.
        synchronized (handler.semaphore) {
            handler.semaphore.wait()
        }
        logger.info("Shutting down")
        handler.close()
        logger.info("Handler terminated")
    }
}
