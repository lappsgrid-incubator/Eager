package org.lappsgrid.eager.rabbitmq

import com.rabbitmq.client.Consumer
import org.junit.Test
import org.lappsgrid.eager.rabbitmq.pubsub.Publisher
import org.lappsgrid.eager.rabbitmq.pubsub.Subscriber

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

import static org.junit.Assert.*

/**
 *
 */
class PublisherTest {
    static final String exchange = 'test.broadcast'

    @Test
    void simple() {
        CountDownLatch latch = new CountDownLatch(1)
        boolean passed = false
        Subscriber subscriber = new Subscriber(exchange)
        subscriber.register { String message ->
            println message
            passed = true
            latch.countDown()
        }
        Publisher publisher = new Publisher(exchange)
        publisher.publish('PublisherTest#simple')
        assert latch.await(3000, TimeUnit.MILLISECONDS)
//        assert 0 == latch.getCount()

    }

    @Test
    void simpleConsumer() {
        CountDownLatch latch = new CountDownLatch(1)
        Subscriber subscriber = new Subscriber(exchange)
        Consumer consumer = new SimpleConsumer(subscriber.channel) {
            @Override
            void consume(String message) {
                println message
                latch.countDown()
            }
        }
        subscriber.register(consumer)

        Publisher publisher = new Publisher(exchange)
        publisher.publish("PublisherTest#simpleConsumer")
        assert latch.await(2, TimeUnit.SECONDS)
    }

    @Test
    void closure() {
        // Number of subscribers
        int n = 3
        // number of Messages
        int m = 5

        // Used to count the total number of messages received.
        CountDownLatch latch = new CountDownLatch(n * m)
        // Used to wait for the subscriber threads to start.
        CountDownLatch ready = new CountDownLatch(n)
        List<Subscriber> subscribers = []
        // Start three subscribers.
        n.times { i ->
            Thread.start {
                println "Starting subscriber $i"
                Subscriber subscriber = new Subscriber(exchange)
                subscribers.add(subscriber)
                subscriber.register { msg ->
                    latch.countDown()
                    println "Listener $i -> $msg"
                }
                ready.countDown()
            }

        }

        // Wait for the above threads to finish starting before starting the publisher.
        boolean readyWait = ready.await(5, TimeUnit.SECONDS)
        if (!readyWait) {
            fail "There was a problem starting the subscribers."
        }

        // Broadcast five messages.
        Publisher broadcaster = new Publisher(exchange)
        m.times { i ->
            broadcaster.publish("$i PublisherTest#closure()")
            sleep(100)
        }
        boolean passed = latch.await(3, TimeUnit.SECONDS)

        subscribers.each { it.close() }
        broadcaster.close()

        assert passed
    }

    @Test
    void consumer() {
        int nConsumers = 5
        int nMessages = 3

        CountDownLatch latch = new CountDownLatch(nConsumers * nMessages)
        CountDownLatch ready = new CountDownLatch(nConsumers)

        AtomicLong count = new AtomicLong()
        Publisher publisher = new Publisher(exchange)

        List<Subscriber> subscribers = []
        nConsumers.times { n ->
            Consumer consumer = new SimpleConsumer(publisher.channel) {
                @Override
                void consume(String message) {
                    int c = count.incrementAndGet()
                    println "Consumer ${n}[$c]: $message"
                    latch.countDown()
                }
            }
            Subscriber subscriber = new Subscriber(exchange)
            subscriber.register(consumer)
            subscribers.add(subscriber)
            ready.countDown()
        }

        if (!ready.await(2, TimeUnit.SECONDS)) {
            fail "There was a problem starting the subscribers."
        }
        nMessages.times { n ->
            println "Publishing message $n"
            publisher.publish("$n PublisherTest#consumer")
            sleep(100)
        }

        latch.await(2, TimeUnit.SECONDS)
        subscribers.each { it.close() }
        publisher.close()
        assert latch.count == 0
    }
}
