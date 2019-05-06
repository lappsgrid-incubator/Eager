package org.lappsgrid.eager.rabbitmq.example

import org.lappsgrid.eager.mining.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.example.factory.IWorkerFactory
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue
import org.lappsgrid.eager.rabbitmq.tasks.Worker
import org.lappsgrid.eager.rabbitmq.topic.MessageBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 * The QueueManager waits for messages to arrive on its {@link org.lappsgrid.eager.rabbitmq.topic.MessageBox MessageBox} and
 * dispatches them to a {@link org.lappsgrid.eager.rabbitmq.tasks.TaskQueue TaskQueue}.  The TaskQueue is serviced by
 * the specified number of workers.
 */
class QueueManager extends MessageBox {
    TaskQueue queue
    PostOffice po
    List<Worker> workers

    /**
     * The QueueManager listens for messages to arrive in its mailbox and dispatches them to a ${link TaskQueue}.
     *
     * @param exchange the RabbitMQ message exchange.
     * @param address  the name of our mailbox.
     * @param host     the address of the RabbitMQ server
     * @param qName    the name of the TaskQueue managed by this QueueManager
     * @param factory  factory used to create workers
     * @param size     the number of workers to create
     */
    QueueManager(String exchange, String address, String host, String qName, IWorkerFactory factory, int size = 1) {
        super(exchange, address, host)
        queue = new TaskQueue(qName, host)
        po = new PostOffice(exchange, host)
        workers = [ ]
        size.times {
            workers.add(factory.create(po, queue))
        }
        workers.each { queue.register(it) }
    }

    /**
     * The QueueManager listens for messages to arrive in its mailbox and dispatches them to a ${link TaskQueue}.
     *
     * @param exchange the RabbitMQ message exchange.
     * @param address  the name of our mailbox.
     * @param host     the address of the RabbitMQ server
     * @param qName    the name of the TaskQueue managed by this QueueManager
     * @param theWorkers the list of workers
     */
    QueueManager(String exchange, String address, String host, String qName, List<Worker> theWorkers) {
        super(exchange, address, host)
        queue = new TaskQueue(qName, host)
        po = new PostOffice(exchange, host)
        workers = [ ]
        theWorkers.each {
            workers.add(it)
        }
        workers.each { queue.register(it) }
    }

    @Override
    void close() {
        super.close()
        workers*.close()
        queue.close()
        po.close()
    }

    @Override
    void recv(Message message) {
        queue.send(Serializer.toJson(message))
    }
}
