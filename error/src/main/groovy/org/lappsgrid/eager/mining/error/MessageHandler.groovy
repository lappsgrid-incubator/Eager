package org.lappsgrid.eager.mining.error

import com.codahale.metrics.Meter
import org.lappsgrid.eager.mining.core.Configuration
import org.lappsgrid.eager.mining.core.jmx.Registry
import org.lappsgrid.eager.rabbitmq.pubsub.Subscriber
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.atomic.AtomicLong

/**
 * Logs messages to 'error' on the 'eager.postoffice' exchange.
 */
class MessageHandler implements MessageHandlerMBean {
    // TODO this should be parsed from the log4j2.properties file.
    private static final String LOG_FILE = "/var/log/eager/error.log"

    private static Logger logger = LoggerFactory.getLogger(MessageHandler)
    private Logger errorLogger = LoggerFactory.getLogger("error-logger")

    final Meter count
    final Meter errors

    // RabbitMQ configuration.
    Configuration configuration

    String shutdownKey

    // The mail box log messages will be sent to.
    MailBox box

    // Semaphore used to signal when the thread should terminate.
    private Object semaphore

//    AtomicLong counter

    MessageHandler() {
        this(new Configuration())
    }

    MessageHandler(Configuration config) {
        this.configuration = config
        this.semaphore = new Object()
//        this.counter = new AtomicLong()
        this.shutdownKey = UUID.randomUUID().toString()

        try {
            logger.debug("Creating meters")
            count = Registry.meter("errors", "count")
            errors = Registry.meter("errors", "errors")

            logger.debug("Registering management bean")
            Registry.register(this, "org.lappsgrid.eager.mining.error.MessageHandler:type=MessageHandler")
            logger.debug("Staring JMX reporter")
            Registry.startJmxReporter()
            logger.debug("JMX initialized")
        }
        catch (Exception e) {
            logger.error("Error initializing JMX", e)
        }
    }

    /**
     * Start the message queue listeners.
     */
    void start() {
        box = new MailBox(configuration.POSTOFFICE, configuration.BOX_ERROR) {
            @Override
            void recv(String message) {
//                counter.incrementAndGet()
                if (message.startsWith('shutdown')) {
                    String key = parseCommand(message)
                    if (key == null) {
                        errors.mark()
                        logger.warn("Invalid shutdown message received: {}", message)
                        return
                    }
                    if (key != shutdownKey) {
                        errors.mark()
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
                        errors.mark()
                        return
                    }
                    logger.info("Sending shutdown key to {}", address)
                    PostOffice po = new PostOffice(configuration.POSTOFFICE)
                    po.send(address, shutdownKey)
                    po.close()
                }
                else if (message.startsWith('collect')) {
                    logger.info("Received a collect message")
//                    String[] parts = message.split("\\s+")
                    String address = parseCommand(message)
                    if (address != null) {
                        File logFile = new File(LOG_FILE)
                        String response = collect()
                        send(address, response)
                    }
                    else {
                        errors.mark()
                        logger.error("Received an invalid collect message: {}", message)
                    }
                }
                else {
                    logger.info("Logging {}", message)
                    count.mark()
                    errorLogger.info(message)
                }
            }
        }
        logger.info("Handler waiting for messages")
    }

    long count() {
//        return counter.get()
        count.getCount()
    }

    String collect() {
        File file = new File(LOG_FILE)
        if (!file.exists()) {
            return "Log file ($LOG_FILE) not found."
        }
        return file.text
    }

    String head(int n = 20) {
        String contents = collect();
        List<String> lines = contents.readLines()
        if (lines.size() < n) {
            return contents
        }
        return lines.subList(0, n).join("\n")
    }

    String tail(int n = 20) {
        String contents = collect();
        List<String> lines = contents.readLines()
        if (lines.size() < n) {
            return contents
        }
        int start = lines.size() - n
        return lines.subList(start, lines.size()).join("\n")
    }

    String version() {
        return Version.getVersion()
    }

    void send(String address, String message) {
        PostOffice po = new PostOffice(configuration.POSTOFFICE)
        po.send(address, message)
        po.close()
    }

    void close() {
//        broadcaster.close()
        Registry.stopJmxReporter()
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
