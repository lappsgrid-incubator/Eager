package org.lappsgrid.eager.rabbitmq.topic

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import org.lappsgrid.eager.rabbitmq.RabbitMQ

/**
 *
 */
abstract class MailBox extends RabbitMQ {
    String exchange

    MailBox(String exchange, String address) {
        this(exchange, address, RabbitMQ.DEFAULT_HOST)
    }

    MailBox(String exchange, String address, String host) {
        super('', host)
        channel.exchangeDeclare(exchange, "direct");
        boolean passive = false
        boolean durable = true
        boolean exclusive = false
        boolean autoDelete = true
        this.queueName = channel.queueDeclare('', durable, exclusive, autoDelete, null).getQueue();
        this.channel.queueBind(queueName, exchange, address)
        this.channel.basicConsume(queueName, false, new MailBoxConsumer(this))
    }

    abstract void recv(String message)

    class MailBoxConsumer extends DefaultConsumer {
        MailBox box

        MailBoxConsumer(MailBox box) {
            super(box.channel)
            this.box = box;
        }

        void handleDelivery(String consumerTag, Envelope envelope,
                            AMQP.BasicProperties properties, byte[] body)
                throws IOException {
            String message = new String(body, "UTF-8");
            try {
                box.recv(message)
                channel.basicAck(envelope.deliveryTag, false)
            }
            catch (Exception e) {
                e.printStackTrace()
            }
        }

    }

}
