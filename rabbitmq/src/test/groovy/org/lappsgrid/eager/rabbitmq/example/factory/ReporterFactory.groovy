package org.lappsgrid.eager.rabbitmq.example.factory

import org.lappsgrid.eager.rabbitmq.example.Reporter
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import java.util.concurrent.CountDownLatch

/**
 *
 */
class ReporterFactory implements IWorkerFactory<Reporter> {

    int id = 0
    CountDownLatch latch

    ReporterFactory(CountDownLatch latch) {
        this.latch = latch
    }

    @Override
    Reporter create(PostOffice po, TaskQueue queue) {
        return new Reporter(++id, latch, po, queue)
    }
}
