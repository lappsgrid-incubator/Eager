package org.lappsgrid.eager.rabbitmq.tasks

import com.rabbitmq.client.MessageProperties
import org.lappsgrid.eager.rabbitmq.RabbitMQ

/**
 *
 */
class TaskQueue extends RabbitMQ{

    public TaskQueue(String name) {
        this(name, 'localhost' , true, true)
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


}
