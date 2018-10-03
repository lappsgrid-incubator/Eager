package org.lappsgrid.eager.rabbitmq.pubsub

import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import org.lappsgrid.eager.rabbitmq.RabbitMQ

/**
 *
 * @deprecated These are for testing only.  Use the Publisher class instead.
 */
@Deprecated
class Broadcaster {

    String exchange
    Connection connection
    Channel channel

    Broadcaster() {
        this('default.public.exchange')
    }

    Broadcaster(String exchange) {
        this(exchange, 'eager', 'eager')
    }

    Broadcaster(String exchange, String user, String password) {
        this.exchange = exchange
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(RabbitMQ.DEFAULT_HOST);
        factory.setUsername(user)
        factory.setPassword(password)

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(exchange, "fanout");
    }

    void broadcast(String message) {
        channel.basicPublish(exchange, "", null, message.getBytes());
    }

    void close() {
        channel.close();
        connection.close();
    }
}
