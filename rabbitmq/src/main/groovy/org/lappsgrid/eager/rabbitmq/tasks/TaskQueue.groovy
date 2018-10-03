package org.lappsgrid.eager.rabbitmq.tasks

import com.rabbitmq.client.Consumer
import com.rabbitmq.client.MessageProperties
import org.lappsgrid.eager.rabbitmq.RabbitMQ
import org.lappsgrid.eager.rabbitmq.SimpleConsumer

/**
 *
 */
class TaskQueue extends RabbitMQ{

    public TaskQueue(String name) {
        this(name, RabbitMQ.DEFAULT_HOST , true, true)
    }

    public TaskQueue(String name, String host) {
        this(name, host, true, true)
    }

    public TaskQueue(String name, String host, boolean durable, boolean fair) {
        super(name, host)

        if (fair) {
            channel.basicQos(1)
        }
        boolean exclusive = false
        boolean autoDelete = false
        channel.queueDeclare(name, durable, exclusive, autoDelete, null);
    }

    void send(String message) {
        channel.basicPublish('', queueName, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes())
    }

    void register(Consumer consumer) {
        this.channel.basicConsume(this.queueName, true, consumer)
    }

    void register(Closure cl) {
        SimpleConsumer consumer = new SimpleConsumer(this.channel) {
            @Override
            void consume(String message) {
                cl(message)
            }
        }
        this.channel.basicConsume(queueName, true, consumer)
    }
}
