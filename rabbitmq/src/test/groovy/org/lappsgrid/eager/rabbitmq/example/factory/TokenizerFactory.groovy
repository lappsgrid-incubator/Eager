package org.lappsgrid.eager.rabbitmq.example.factory

import org.lappsgrid.eager.rabbitmq.example.Tokenizer
import org.lappsgrid.eager.rabbitmq.tasks.TaskQueue
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 *  A worker factory for creating Tokenizers.
 */
class TokenizerFactory implements IWorkerFactory<Tokenizer> {
    int id = 0

    @Override
    Tokenizer create(PostOffice po, TaskQueue queue) {
        ++id
        return new Tokenizer(id, queue, po)
    }
}
