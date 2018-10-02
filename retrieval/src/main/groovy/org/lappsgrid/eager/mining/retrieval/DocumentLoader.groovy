package org.lappsgrid.eager.mining.retrieval

import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 *
 */
class DocumentLoader {

    /**
     * Semaphore object used to block the main thread until a shutdown
     * message has been received.
     */
    private Object semaphore

    Configuration config
    PostOffice office
    MailBox box

    DocumentLoader() {
        this(new Configuration())
    }

    DocumentLoader(Configuration configuration) {
        this.config = configuration
        office = new PostOffice(config.POSTOFFICE)
        semaphore = new Object()
    }

    void start() {
        println "DocumentLoader starting"
        box = new MailBox(config.POSTOFFICE, config.BOX_LOAD) {
            @Override
            void recv(String message) {
                if (message == 'shutdown') {
                    stop()
                    return
                }
                process(message)
            }
        }
    }

    void process(String json) {
        Message message
        try {
            message = Serializer.parse(json, Message)
        }
        catch (Exception e) {
            //TODO Send a message to the error service as well.
            e.printStackTrace()
            return
        }
        if (message.command == 'shutdown') {
            println "Shutdown message received."
            stop()
        }
        else if (message.command == 'load') {
            File file = new File(message.body)
            if (!file.exists()) {
                // TODO Send a message to the error service.
                println "File not found: ${file.path}"
                return
            }
            message.command = 'loaded'
            message.body = file.text
            office.send(message)
        }
        else {
            // TODO Send a message to the error service.
            String error = "Unknown command: ${message.command}"
            println error
            message.command = 'error'
            message.body(error)
            office.send(message)
        }

    }

    void stop() {
        close()
        synchronized (semaphore) {
            semaphore.notifyAll()
        }
    }

    void close() {
        office.close()
        box.close()
    }

    public static void main(String[] args) {
        DocumentLoader loader = new DocumentLoader()
        loader.start()
        synchronized (loader.semaphore) {
            loader.semaphore.wait()
        }
        println "DocumentLoader has shutdown."
    }
}
