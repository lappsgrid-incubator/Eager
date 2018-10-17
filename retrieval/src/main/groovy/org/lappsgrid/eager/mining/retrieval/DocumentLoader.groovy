package org.lappsgrid.eager.mining.retrieval

import com.codahale.metrics.Meter
import com.codahale.metrics.MetricRegistry
import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.management.MBeanServer
import javax.management.ObjectName
import java.lang.management.ManagementFactory

/**
 *
 */
class DocumentLoader implements DocumentLoaderMBean {

    static final MetricRegistry metrics = new MetricRegistry()

    final Logger logger = LoggerFactory.getLogger(DocumentLoader)

    /**
     * Semaphore object used to block the main thread until a shutdown
     * message has been received.
     */
    private final Object semaphore

    final Configuration config
    final PostOffice office
    final MailBox box

    final Meter received
    final Meter errors
    final Meter exceptions

    DocumentLoader() {
        this(new Configuration())
    }

    DocumentLoader(Configuration configuration) {
        this.config = configuration
        office = new PostOffice(config.POSTOFFICE)
        semaphore = new Object()
        box = new MailBox(config.POSTOFFICE, config.BOX_LOAD) {
            @Override
            void recv(String message) {
                if (message == 'shutdown') {
                    //stop()
                    logger.warn 'Shutdown message ignored.'
                    return
                }
                process(message)
            }
        }

        received = metrics.meter(name("loader", "received"))
        exceptions = metrics.meter(name("loader", "exceptions"))
        errors = metrics.meter(name("loader", "errors"))

        MBeanServer server = ManagementFactory.getPlatformMBeanServer()
        server.registerMBean(this, new ObjectName("org.lappsgrid.eager.mining.retrieval:type=DocumentLoader"))
    }

    void process(String json) {
        received.mark()
        Message message
        try {
            message = Serializer.parse(json, Message)
        }
        catch (Exception e) {
            //TODO Send a message to the error service as well.
            exceptions.mark()
            logger.error("Unable to deserialize JSON message", e)
            return
        }
        if (message.command == 'shutdown') {
            logger.warn "Shutdown message ignored."
            return
        }
        else if (message.command == 'load') {
            File file = new File(message.body)
            if (!file.exists()) {
                // TODO Send a message to the error service.
                errors.mark()
                String error = "File not found: ${file.path}"
                logger.warn(error)
                message.command = 'error'
                message.parameters['document.load.error'] = error
                office.send(message)
                return
            }
            message.command = 'loaded'
            message.body = file.text
            message.parameters.path = file.path
            logger.info("Loaded {}", file.path)
            logger.debug("Sending reply to {}", message.route[0])
            office.send(message)
        }
        else {
            // TODO Send a message to the error service.
            errors.mark()
            String error = "Unknown command: ${message.command}"
            logger.error(error)
            message.command = 'error'
            message.parameters['document.load.error'] = error
            office.send(message)
        }

    }

    void stop() {
        logger.info("Stopping")
        synchronized (semaphore) {
            semaphore.notifyAll()
        }
    }

    void close() {
        logger.info("Closing")
        office.close()
        box.close()
    }

    String name(String... parts) {
        return this.class.package.name + "." + parts.join(".")
    }

    public static void main(String[] args) {
        DocumentLoader loader = new DocumentLoader()

        // Wait for another thread to notify() us.
        synchronized (loader.semaphore) {
            loader.semaphore.wait()
        }

        // We have been notified, time to exit.
        loader.close()
        println "DocumentLoader has shutdown."
    }
}
