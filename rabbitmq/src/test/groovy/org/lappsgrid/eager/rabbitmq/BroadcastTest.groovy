package org.lappsgrid.eager.rabbitmq

import com.rabbitmq.client.AMQP
import com.rabbitmq.client.Channel
import com.rabbitmq.client.Consumer
import com.rabbitmq.client.DefaultConsumer
import com.rabbitmq.client.Envelope
import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.rabbitmq.pubsub.Broadcaster
import org.lappsgrid.eager.rabbitmq.pubsub.Listener

import java.util.concurrent.atomic.AtomicInteger

/**
 *
 */
@Ignore
class BroadcastTest {
    public static final String exchange = 'test.public.broadcast'

    @Test
    void broadcast() {
        AtomicInteger count = new AtomicInteger()

        Listener listener = new Listener(exchange)
        listener.register { String message ->
            int n = count.incrementAndGet()
            println "$n: $message"
        }

        Broadcaster broadcaster = new Broadcaster(exchange)
        5.times { n ->
            broadcaster.broadcast("${n}. BroadcastTest#broadcast() on $exchange")
            sleep(100)
        }
        sleep(1000)
        broadcaster.close()
        listener.close()
        assert 5 == count.intValue()
    }

    @Test
    void consumer() {
        Listener listener = new Listener(exchange)
        boolean passed = false
        Consumer consumer = new DefaultConsumer(listener.channel) {
            void handleDelivery(String consumerTag, Envelope envelope,
                                AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");
                try {
                    println message
                    passed = true
                }
                catch (Exception e) {
                    e.printStackTrace()
                }
            }

        }
        listener.register(consumer)
        Broadcaster broadcaster = new Broadcaster(exchange)
        broadcaster.broadcast("BroadcastTest#consumer()")
        sleep(2000)
        assert passed
    }

    @Test
    void custom() {
        Object lock = new Object()
        Listener listener = new Listener(exchange)
        boolean passed = false
        Consumer consumer = new SimpleConsumer(listener.channel) {
            @Override
            void consume(String message) {
                println message
                passed = true
                synchronized (lock) {
                    lock.notifyAll()
                }
            }
        }
        listener.register(consumer)

        Broadcaster broadcaster = new Broadcaster(exchange)
        broadcaster.broadcast("BroadcastTest#custom()")
        synchronized (lock) {
            lock.wait(2000)
        }
        assert passed
    }

//    class CustomConsumer extends DefaultConsumer {
//
//        boolean passed
//
//        CustomConsumer(Channel channel) {
//            super(channel)
//            passed = false
//        }
//
//        void handleDelivery(String consumerTag, Envelope envelope,
//                            AMQP.BasicProperties properties, byte[] body)
//                throws IOException {
//            String message = new String(body, "UTF-8");
//            try {
//                println message
//                passed = true
//            }
//            catch (Exception e) {
//                e.printStackTrace()
//            }
//        }
//    }
}
