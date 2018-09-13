package org.lappsgrid.eager.mining.solr.api

import java.util.concurrent.BlockingQueue

/**
 * Workers read data from an input queue, transform the data, and then write
 * the result to an output queue.
 */
abstract class Worker extends Haltable {
    public static final Object DONE = new Object()

    String name
    BlockingQueue<Object> input
    BlockingQueue<Object> output

    public Worker(String name, BlockingQueue<Object> input, BlockingQueue<Object> output) {
        this.name = name
        this.input = input
        this.output = output
        println "Worker $name input $input  output $output"
    }

    @Override
    void run() {
        println "Starting worker $name"
        running = true
        while(running) {
            try {
                Object item = input.take()
                output.put(work(item))
//                if (item == DONE) {
//                    output.put(DONE)
//                    running = false
//                }
//                else {
//                    output.put(work(item))
//                }
            }
            catch (InterruptedException e) {
                running = false
                Thread.currentThread().interrupt()
            }
        }
        println "Thread $name terminated."
    }

    abstract Object work(Object item);
}
