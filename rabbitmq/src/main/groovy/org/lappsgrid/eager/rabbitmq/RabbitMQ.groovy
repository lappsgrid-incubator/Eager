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
    public static final String DEFAULT_HOST = 'rabbitmq.lappsgrid.org'
//    public static final String DEFAULT_HOST = 'localhost'

    String queueName
    Connection connection
    Channel channel
    boolean ack

    RabbitMQ(String queueName) {
        this(queueName, DEFAULT_HOST)
    }

    RabbitMQ(String queueName, String host) {
        ConnectionFactory factory = new ConnectionFactory()
        factory.setHost(host)
        factory.setUsername('eager')
        factory.setPassword('eager')
        connection = factory.newConnection()
        channel = connection.createChannel()
        this.ack = true
        this.queueName = queueName
    }

    void register(Consumer consumer) {
//        register(consumer, false)
//    }
//
//    void register(Consumer consumer, boolean autoAck) {
//        this.ack = ! autoAck
        channel.basicConsume(queueName, false, consumer)
    }

    void register(Closure cl) {
//        register(false, cl)
//    }
//
//    void register(boolean autoAck, Closure cl) {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                cl(message)
                if (ack) {
                    channel.basicAck(envelope.deliveryTag, false)
                }
            }
        }
        register(consumer)
    }

    void close() {
        if (channel.isOpen()) {
            channel.close()
        }
        if (connection.isOpen()) {
            connection.close()
        }
    }
}
