package org.lappsgrid.eager.rabbitmq.pubsub

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

/**
 *
 */
abstract class Subscriber extends DefaultConsumer {
    private Publisher broadcaster

    Subscriber(String name) {
        this(name, 'localhost')
    }

    Subscriber(String name, String host) {
        this(new Publisher(name, host))
    }

    Subscriber(Publisher broadcaster) {
        super(broadcaster.channel)
        this.broadcaster = broadcaster
        broadcaster.channel.basicConsume(broadcaster.queue, true, this)
    }

    void handleDelivery(String consumerTag, Envelope envelope,
                        AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        String message = new String(body, "UTF-8");
        try {
//            println "Subscriber recv: $message"
            recv(message)
        }
        catch (Exception e) {
            e.printStackTrace()
        }
    }

    void close() {
        broadcaster.close()
    }

    abstract void recv(String message)
}
