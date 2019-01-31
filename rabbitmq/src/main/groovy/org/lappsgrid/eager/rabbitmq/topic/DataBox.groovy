package org.lappsgrid.eager.rabbitmq.topic

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import org.lappsgrid.eager.rabbitmq.RabbitMQ

/**
 *
 */
abstract class DataBox extends RabbitMQ {
    String exchange

    DataBox(String exchange, String address) {
        this(exchange, address, RabbitMQ.DEFAULT_HOST)
    }

    DataBox(String exchange, String address, String host) {
        super('', host)
        channel.exchangeDeclare(exchange, "direct");
        boolean passive = false
        boolean durable = true
        boolean exclusive = false
        boolean autoDelete = true
        this.queueName = channel.queueDeclare('', durable, exclusive, autoDelete, null).getQueue();
        this.channel.queueBind(queueName, exchange, address)
        this.channel.basicConsume(queueName, false, new DataBoxConsumer(this))
    }

    abstract void recv(byte[] message)

    class DataBoxConsumer extends DefaultConsumer {
        DataBox box

        DataBoxConsumer(DataBox box) {
            super(box.channel)
            this.box = box;
        }

        void handleDelivery(String consumerTag, Envelope envelope,
                            AMQP.BasicProperties properties, byte[] body)
                throws IOException {
            try {
                box.recv(body)
                channel.basicAck(envelope.deliveryTag, false)
            }
            catch (Exception e) {
                e.printStackTrace()
            }
        }

    }

}
