package org.lappsgrid.eager.rabbitmq.example

import org.lappsgrid.eager.mining.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue
import org.lappsgrid.eager.rabbitmq.tasks.Worker
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 * Sorts the message body, which is expected to be an array of strings.
 */
class Sorter extends Worker {
    int id
    PostOffice po

    Sorter(int id, TaskQueue queue, PostOffice po) {
        super(queue)
        this.id = id
        this.po = po
    }

    @Override
    void work(String json) {
        // Parse the message
        Message message = Serializer.parse(json, Message)
        // Do the work
        String msgid = message.get("id")
        println "Sorter $id message: $msgid"
        String[] tokens = (String[]) message.body
        message.body = tokens.sort()
        // And forward the message
        po.send(message)
    }
}
