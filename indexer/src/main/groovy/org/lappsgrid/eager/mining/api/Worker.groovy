package org.lappsgrid.eager.mining.api

import groovy.util.logging.Slf4j

import java.util.concurrent.BlockingQueue

/**
 * Workers read data from an input queue, transform the data, and then write
 * the result to an output queue.
 */
@Slf4j("logger")
abstract class Worker extends Haltable {
    static final Object DONE = new Object()

    String name
    BlockingQueue<Object> input
    BlockingQueue<Object> output

    public Worker(String name, BlockingQueue<Object> input, BlockingQueue<Object> output) {
        this.name = name
        this.input = input
        this.output = output
    }

    @Override
    void run() {
        logger.info("Starting worker {}", name)
        running = true
        while(running) {
            try {
                // Take an item from the input queue
                Object item = input.take()
                // Process it
                item = work(item)
                // And put it on the output queue
                output.put(item)
            }
            catch (InterruptedException e) {
                running = false
                Thread.currentThread().interrupt()
            }
        }
        logger.info("Thread {} terminated.", name)
    }

    abstract Object work(Object item);
}
