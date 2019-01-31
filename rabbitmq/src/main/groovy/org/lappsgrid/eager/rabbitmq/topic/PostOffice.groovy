package org.lappsgrid.eager.rabbitmq.topic

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Connection
import com.rabbitmq.client.ConnectionFactory
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import com.rabbitmq.client.MessageProperties
import groovy.json.JsonOutput
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.RabbitMQ

/**
 *
 */
class PostOffice extends RabbitMQ {
    String exchange

    PostOffice(String exchange) {
        this(exchange, RabbitMQ.DEFAULT_HOST)
    }

    PostOffice(String exchange, String host) {
        super('', host)
        this.exchange = exchange
        channel.exchangeDeclare(exchange, 'direct')
    }

    void send(Message message) {
        if (message.route.size() == 0) {
            println "Nowhere to send message"
            return
        }
        String address = message.route.remove(0)
        String json = JsonOutput.toJson(message)
        send(address, json)
    }

    void send(String address, String message) {
//        println "Sending ${message.bytes.length} bytes to $address"
//        channel.basicPublis,h(exchange, address, MessageProperties.PERSISTENT_TEXT_PLAIN, message.bytes)
        send(address, message.bytes)
    }

    void send(String address, byte[] data) {
        channel.basicPublish(exchange, address, MessageProperties.PERSISTENT_TEXT_PLAIN, data)
    }
}
