package org.lappsgrid.eager.rabbitmq.pubsub

import com.rabbitmq.client.MessageProperties
import org.lappsgrid.eager.rabbitmq.RabbitMQ


/**
 *
 */
class Publisher extends RabbitMQ {
    String exchange
    String queue

    public Publisher(String exchange) {
        this(exchange, DEFAULT_HOST , true)
    }

    public Publisher(String exchange, String host) {
        this(exchange, host, true)
    }

    public Publisher(String exchange, String host, boolean durable) {
        super('', host)
        this.exchange = exchange

        channel.exchangeDeclare(exchange, "fanout")
        queue = channel.queueDeclare().queue
        String routingKey = ""
        channel.queueBind(queue, exchange, routingKey)
    }

    void publish(String message) {
        channel.basicPublish(exchange, '', null, message.bytes)
    }

}
