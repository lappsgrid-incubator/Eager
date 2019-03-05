package org.lappsgrid.eager.rabbitmq.example

import org.lappsgrid.eager.mining.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.tasks.Worker
import org.lappsgrid.eager.rabbitmq.topic.MessageBox

import java.util.concurrent.CountDownLatch

/**
 * The Reporter is the final worker in the pipeline and simply writes the message to System.out as JSON.
 */
class Reporter extends Worker {

    int id
    CountDownLatch latch

    Reporter(int id, CountDownLatch latch, TaskQueue queue) {
        super(queue)
        this.id = id
        this.latch = latch
    }

    @Override
    void work(String json) {
        Message message = Serializer.parse(json, Message)
        message.set("reporter", "$id")
        println Serializer.toPrettyJson(message)
        latch.countDown()
    }
}
