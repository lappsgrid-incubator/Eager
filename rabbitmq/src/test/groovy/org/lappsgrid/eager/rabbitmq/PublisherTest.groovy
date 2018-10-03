package org.lappsgrid.eager.rabbitmq

import org.junit.Test
import org.lappsgrid.eager.rabbitmq.pubsub.Publisher
import org.lappsgrid.eager.rabbitmq.pubsub.Subscriber

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 *
 */
class PublisherTest {

    @Test
    void broadcast() {
        AtomicInteger count = new AtomicInteger()
        String name = 'test.broadcast'

        List<Publisher> publishers = []
        // Start three subscribers.
        3.times { n ->
            Thread.start {
                println "Staring listener $n"
                Publisher b = new Publisher(name)
                publishers.add(b)
                b.register { msg ->
                    count.incrementAndGet()
                    println "Listener $n -> $msg"
                }
            }

        }

        // Broadcast five messages.
        Publisher broadcaster = new Publisher(name)
        publishers.add(broadcaster)
        5.times { i ->
            broadcaster.publish("$i Hello world")
            sleep(500)
        }
        sleep(1000)
        publishers*.close()
        assert 15 == count.intValue()
        println "Count: ${count.get()}"
    }

    @Test
    void subscriber() {
        AtomicLong count = new AtomicLong()
        String name = 'test.broadcast'
        3.times { id ->
            new Subscriber(name) {

                @Override
                void recv(String message) {
                    long n = count.incrementAndGet()
                    println "Subscriber $id : $n : $message"
                }
            }
        }
        Publisher broadcaster = new Publisher(name)
        5.times { i ->
            broadcaster.publish("$i Hello world")
            sleep(100)
        }
        sleep(1000)
        int n = count.intValue()
        println "Count: $n"
        assert 15 == n
    }
}
