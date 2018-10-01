package org.lappsgrid.eager.mining.nlp.stanford

import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.DataContainer
import org.lappsgrid.serialization.Serializer

/**
 *
 */
class Main {

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
        box = new MailBox(config.POSTOFFICE, config.BOX_NLP_STANFORD) {
            @Override
            void recv(String json) {
                Message message = Serializer.parse(json, Message)
                if ('shutdown' == message.command) {
                    stop()
                    return
                }
                if (message.route.size() == 0) {
                    // If there is nowhere to send the result then we have nothing to do.
                    error("NLP tools were sent data but have no route defined.")
                    return
                }

                DataContainer data
                try {
                    data = Serializer.parse(message.body, DataContainer)
                    data.payload = pipeline.process(data.payload)

                }
                catch (Exception e) {
                    error("NLP tools encountered an exception: " + e.message)
                    return
                }
                message.body = data.asJson()
                post.send(message)

            }
        }
    }

    void stop() {
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

    }
}
