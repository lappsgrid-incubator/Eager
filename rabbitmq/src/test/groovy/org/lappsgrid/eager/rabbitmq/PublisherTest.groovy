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
        String name = 'broadcast'

        3.times { n ->
            Thread.start {
                println "Staring listener $n"
                Publisher b = new Publisher(name)
                b.register { msg ->
                    count.incrementAndGet()
                    println "Listener $n -> $msg"
                }
            }

        }
        Publisher broadcaster = new Publisher(name)
        5.times { i ->
            broadcaster.publish("$i Hello world")
            sleep(500)
        }
        sleep(5000)
        assert 15 == count.intValue()
        println "Count: ${count.get()}"
    }

    @Test
    void subscriber() {
        AtomicLong count = new AtomicLong()
        String name = 'broadcast'
        3.times { id ->
            new Subscriber(name) {

                @Override
                void recv(String message) {
                    count.incrementAndGet()
                    println "Subscriber $id: $message"
                }
            }
        }
        Publisher broadcaster = new Publisher(name)
        5.times { i ->
            broadcaster.publish("$i Hello world")
            sleep(100)
        }
        sleep(2000)
        assert 15 == count.intValue()
        println "Count: ${count.get()}"
    }
}
