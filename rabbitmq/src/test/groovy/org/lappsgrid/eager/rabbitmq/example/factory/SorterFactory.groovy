package org.lappsgrid.eager.rabbitmq.example.factory

import org.lappsgrid.eager.rabbitmq.example.Sorter
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 *  A worker factory that knows how to produce @{link Sorter} instances.
 */
class SorterFactory implements IWorkerFactory<Sorter> {
    int id = 0

    @Override
    Sorter create(PostOffice po, TaskQueue queue) {
        ++id
        return new Sorter(id, queue, po)
    }
}
