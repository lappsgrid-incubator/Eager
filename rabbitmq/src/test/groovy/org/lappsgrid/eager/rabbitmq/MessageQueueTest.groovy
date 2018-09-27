package org.lappsgrid.eager.rabbitmq

import org.junit.*
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue
import org.lappsgrid.eager.rabbitmq.tasks.Worker

/**
 *
 */
class MessageQueueTest {
    @Test
    void send() {
//        MessageQueue q = new MessageQueue('queue', 'localhost', false, false)
        TaskQueue q = new TaskQueue('queue')
        q.send("1. Hello world.")
        q.send("2. Hello world.")
        q.send("3. Hello world.")
        println "Message sent"
    }

    @Test
    void recv() {
//        MessageQueue q = new MessageQueue('queue', 'localhost', false, false)
        TaskQueue q = new TaskQueue('queue')
        q.register { String message ->
            println "Registered closure: $message"
        }
        Thread.sleep(2000)
        println "Done"
    }

    @Test
    void workers() {
        TaskQueue q = new TaskQueue('queue')
        q.register { msg ->
            println "1 $msg"
        }
        new TestWorker(q)

        q.send("one")
        q.send("two")
        q.send("three")
        q.send("four")
        Thread.sleep(2000)
        println "Done"
    }

    synchronized void sleep() {
        this.wait()
    }

    class TestWorker extends Worker {
        TestWorker(TaskQueue q) {
            super(q)

        }

        @Override
        void work(String message) {
            println "2 $message"
        }
    }
}
