package org.lappsgrid.eager.rabbitmq.pubsub

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import org.lappsgrid.eager.rabbitmq.RabbitMQ

/**
 *
 */
class Subscriber {
    String exchange
    String queueName
    Connection connection
    Channel channel

    Subscriber(String exchange) {
        this(exchange, RabbitMQ.DEFAULT_HOST)
    }

    Subscriber(String exchange, String host) {
        this.exchange = exchange
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername('eager')
        factory.setPassword('eager')

        connection = factory.newConnection();
        channel = connection.createChannel();

        channel.exchangeDeclare(exchange, "fanout");
        queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, exchange, "");
    }

    void register(Consumer consumer) {
        channel.basicConsume(queueName, true, consumer)
    }

    void register(Closure cl) {
        Consumer consumer = new DefaultConsumer(this.channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                cl(message)
            }
        }
        register(consumer)
    }

    void close() {
        if (channel.isOpen()) channel.close()
        if (connection.isOpen()) connection.close()
    }
}
