package org.lappsgrid.eager.rabbitmq.tasks

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import org.lappsgrid.eager.rabbitmq.RabbitMQ

/**
 *
 */
abstract class Worker extends DefaultConsumer {

    private TaskQueue queue

    Worker(String name) {
        this(new TaskQueue(name, RabbitMQ.DEFAULT_HOST))
    }
    Worker(String name, String host) {
        this(new TaskQueue(name, host))
    }
    Worker(TaskQueue tq) {
        super(tq.channel)
        this.queue = tq
        //this.queue.register(this)
        queue.channel.basicConsume(tq.queueName, true, this)

    }

    void close() {
        queue.close()
    }

    void handleDelivery(String consumerTag, Envelope envelope,
                        AMQP.BasicProperties properties, byte[] body)
            throws IOException {
        String message = new String(body, "UTF-8");
        try {
            work(message)
        }
        catch (Exception e) {
            e.printStackTrace()
        }
        this.channel.basicAck(envelope.deliveryTag, false)
    }

    abstract void work(String message)

}
