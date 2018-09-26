package org.lappsgrid.eager.rabbitmq.topic

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.rabbitmq.client.MessageProperties
import org.lappsgrid.eager.rabbitmq.RabbitMQ

/**
 *
 */
class PostOffice extends RabbitMQ {
    String exchange

    PostOffice(String exchange) {
        this(exchange, 'localhost')
    }

    PostOffice(String exchange, String host) {
        super('', host)
        this.exchange = exchange
        channel.exchangeDeclare(exchange, 'direct')
    }

    void send(String address, String message) {
        channel.basicPublish(exchange, address, MessageProperties.PERSISTENT_TEXT_PLAIN, message.bytes)
    }
}
