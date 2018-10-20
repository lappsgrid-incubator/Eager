package org.lappsgrid.eager.rabbitmq.topic

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.RabbitMQ

/**
 *
 */
abstract class MessageBox extends RabbitMQ {
    String exchange

    MessageBox(String exchange, String address) {
        this(exchange, address, RabbitMQ.DEFAULT_HOST)
    }

    MessageBox(String exchange, String address, String host) {
        super('', host)
        channel.exchangeDeclare(exchange, "direct");
        boolean passive = false
        boolean durable = true
        boolean exclusive = false
        boolean autoDelete = true
        this.queueName = channel.queueDeclare('', durable, exclusive, autoDelete, null).getQueue();
        this.channel.queueBind(queueName, exchange, address)
        this.channel.basicConsume(queueName, false, new MessageBoxConsumer(this))
    }

    abstract void recv(Message message)

    class MessageBoxConsumer extends DefaultConsumer {
        MessageBox box

        MessageBoxConsumer(MessageBox box) {
            super(box.channel)
            this.box = box;
        }

        void handleDelivery(String consumerTag, Envelope envelope,
                            AMQP.BasicProperties properties, byte[] body)
                throws IOException {
            String json = new String(body, "UTF-8");
            try {
                Message message = Serializer.parse(json, Message)
                box.recv(message)
                channel.basicAck(envelope.deliveryTag, false)
            }
            catch (Exception e) {
                e.printStackTrace()
            }
        }

    }

}
