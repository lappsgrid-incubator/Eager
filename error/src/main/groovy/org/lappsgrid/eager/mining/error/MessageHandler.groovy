package org.lappsgrid.eager.mining.error

import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.rabbitmq.pubsub.Subscriber
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicLong

/**
 * Logs messages to 'error' on the 'eager.postoffice' exchange.
 */
class MessageHandler {
    private static Logger logger = LoggerFactory.getLogger(MessageHandler)
    private Logger errorLogger = LoggerFactory.getLogger("error-logger")

    // RabbitMQ configuration.
    Configuration configuration

    // Listen for broadcast messages.
    Subscriber broadcaster

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
    }

    /**
     * Start the message queue listeners.
     */
    void start() {
        /*
        broadcaster = new Subscriber(configuration.BROADCAST) {
            @Override
            void recv(String message) {
                if (message == 'shutdown') {
                    logger.info('Received a shutdown message.')
                    synchronized (semaphore) {
                        semaphore.notifyAll()
                    }
                }
                else if (message == 'ping') {
                    println "pong"
                }
                else {
                    logger.warn("Received an unhandled broadcast message: {}", message)
                }
            }
        }
        */
        box = new MailBox(configuration.POSTOFFICE, configuration.BOX_ERROR) {
            @Override
            void recv(String message) {
                counter.incrementAndGet()
                if (message == 'shutdown') {
                    logger.info('Received a shutdown message')
                    synchronized (semaphore) {
                        semaphore.notifyAll()
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

    void close() {
//        broadcaster.close()
        box.close()
        logger.info('Closed all connections.')
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
