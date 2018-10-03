package org.lappsgrid.eager.rabbitmq

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

/**
 *
 */
abstract class SimpleConsumer extends DefaultConsumer {
    SimpleConsumer(Channel channel) {
        super(channel)
    }

    abstract void consume(String message)

    @Override
    void handleDelivery(String consumerTag, Envelope envelope,
                        AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        String message = new String(body, "UTF-8");
        try {
            consume(message)
        }
        catch (Exception e) {
            e.printStackTrace()
        }
    }

}
