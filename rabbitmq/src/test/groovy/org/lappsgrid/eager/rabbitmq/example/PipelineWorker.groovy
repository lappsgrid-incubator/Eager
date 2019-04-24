package org.lappsgrid.eager.rabbitmq.example

import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.tasks.Worker
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue

/**
 *
 */
abstract class PipelineWorker extends Worker {
    int id
    PostOffice po

    PipelineWorker(int id, PostOffice po, TaskQueue queue) {
        super(queue)
        this.id = id
        this.po = po
    }

    abstract String name()

    void stats() {
        Map data = [
            id: "${name()}-${id}",
            thread: Thread.currentThread().name
        ]
        Message message = new Message().body(data).route("stats.mbox")
        po.send(message)
    }
}
