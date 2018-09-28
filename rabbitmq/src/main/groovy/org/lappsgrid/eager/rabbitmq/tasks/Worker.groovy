package org.lappsgrid.eager.rabbitmq.tasks

import com.rabbitmq.client.AMQP
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
    Worker(TaskQueue queue) {
        super(queue.channel)
        this.queue = queue
        this.queue.register(this)
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

//    Worker(String name, Closure cl) {
//       this(name, 'localhost', true, true, cl)
//    }
//
//    Worker(String name, String host, Closure cl) {
//        this(name, host, true, true, cl)
//    }
//
//    Worker(String name, String host, boolean durable, boolean fair, Closure cl) {
//        this(name, host, durable, fair)
//        this.queue.register(cl)
//    }

//    class Consumer extends DefaultConsumer {
//
//        Consumer() {
//            super(queue.channel)
//        }
//
//        void handleDelivery(String consumerTag, Envelope envelope,
//                            AMQP.BasicProperties properties, byte[] body)
//                throws IOException {
//            String message = new String(body, "UTF-8");
//            println "Worker handling message: $message"
//            boolean success = false
//            try {
//                if (handler != null) {
//                    success = handler(message)
//                }
//                else {
//                    success = work(message)
//                }
//            }
//            catch (Exception e) {
//                e.printStackTrace()
//            }
//            println "ack: $success"
//            queue.channel.basicAck(envelope.deliveryTag, success)
//        }
//    }

}
