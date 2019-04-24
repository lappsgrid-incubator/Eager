package org.lappsgrid.eager.rabbitmq.example.factory

import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue

/**
 * Factory interface used by {@link QueueManager} instances to spawn new workers.
 */
interface IWorkerFactory<T> {
    T create(PostOffice po, TaskQueue queue)
}