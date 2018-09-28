package org.lappsgrid.eager.rabbitmq

import org.junit.*
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue
import org.lappsgrid.eager.rabbitmq.tasks.Worker

import java.util.concurrent.atomic.AtomicInteger

/**
 *
 */
class MessageQueueTest {

    @Test
    void simple() {
        int n = 0
        TaskQueue q = new TaskQueue('test.queue')
        q.register { String message ->
            println message
            ++n
        }

        q.send('hello world')
        assert 1 == n
    }

    @Test
    void send() {
//        MessageQueue q = new MessageQueue('queue', 'localhost', false, false)
        AtomicInteger count = new AtomicInteger()
        TaskQueue r = new TaskQueue('test.queue')
        r.register { String message ->
            count.incrementAndGet()
            println "Registered closure: $message"
        }
        TaskQueue s = new TaskQueue('test.queue')
        s.send("1. Hello world.")
        s.send("2. Hello world.")
        s.send("3. Hello world.")
        println "Messages sent"
        sleep(2000)
        assert 3 == count.intValue()
    }

    @Test
    void workers() {
        TaskQueue q = new TaskQueue('test.queue')
        AtomicInteger n = new AtomicInteger()
        q.register { msg ->
            n.incrementAndGet()
            println "1 $msg"
        }
        TestWorker w = new TestWorker(q)

        q.send("one")
        q.send("two")
        q.send("three")
        q.send("four")
        Thread.sleep(2000)
        assert 2 == n.intValue()
        assert 2 == w.n

        println "Done"
    }

    class TestWorker extends Worker {
        int n

        TestWorker(TaskQueue q) {
            super(q)
        }

        @Override
        void work(String message) {
            ++n
            println "2 $message"
        }
    }
}
