package org.lappsgrid.eager.mining.retrieval

import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
//import org.lappsgrid.serialization.Serializer

import com.lambdaworks.redis.*;

/**
 *
 */
class RedisLoader {

    /**
     * Semaphore object used to block the main thread until a shutdown
     * message has been received.
     */
    private Object semaphore

//    RedisClient redis

    Configuration config
    PostOffice office
    MailBox box

    RedisLoader() {
        this(new Configuration())
    }

    RedisLoader(Configuration configuration) {
        this.config = configuration
        office = new PostOffice(config.POSTOFFICE)
//        RedisURI uri = RedisURI.create("redis://password@localhost:6379")
//        redis = new RedisClient(uri)
    }

    void start() {
        box = new MailBox(config.properties, config.BOX_REDIS) {
            @Override
            void recv(String message) {
                process(message)
            }
        }
    }

    void process(String json) {
        Message message = Serializer.parse(json, Message)
        if (message.command == 'shutdown') {
            // Notify the main thread that they are done waiting.
            synchronized (semaphore) {
                semaphore.notifyAll()
            }
        }
        else {
            println json
        }

    }

    void close() {
        office.close()
        box.close()
    }

    private void error(String message) {
        office.send(config.BOX_ERROR, message)
    }

    public static void main(String[] args) {
        RedisLoader fetch = new RedisLoader()
        fetch.start()

        // Wait forever for another thread to notify us that we should exit.
        synchronized (fetch.semaphore) {
            fetch.semaphore.wait()
        }
        fetch.close()


    }
}
