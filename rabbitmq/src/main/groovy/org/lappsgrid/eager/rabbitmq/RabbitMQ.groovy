package org.lappsgrid.eager.rabbitmq

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope

/**
 *
 */
class RabbitMQ {
    String queueName
    Connection connection
    Channel channel
    boolean ack

    RabbitMQ(String queueName) {
        this(queueName, 'localhost')
    }

    RabbitMQ(String queueName, String host) {
        ConnectionFactory factory = new ConnectionFactory()
        factory.setHost(host)
        factory.setUsername('admin')
        factory.setPassword('password')
        connection = factory.newConnection()
        channel = connection.createChannel()
        this.ack = false
        this.queueName = queueName
    }

    void register(Consumer consumer) {
        channel.basicConsume(queueName, true, consumer)
    }

    void register(Closure cl) {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                boolean success = cl(message)
                if (ack) {
                    channel.basicAck(envelope.deliveryTag, success)
                }
            }
        };
        register(consumer)
    }

    void close() {
        channel.close()
        connection.close()
    }
}
