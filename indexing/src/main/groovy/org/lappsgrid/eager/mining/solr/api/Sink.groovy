package org.lappsgrid.eager.mining.solr.api

import java.util.concurrent.BlockingQueue

/**
 * A Sink takes objects from its input queue and stores them somewhere.
 */
abstract class Sink extends Haltable {

    String name
    BlockingQueue<Object> input
    int total

    public Sink(String name, BlockingQueue<Object> input) {
        this.name = name
        this.input = input
        this.total = -1
    }

    @Override
    void run() {
        println "Starting sink $name"
        int count = 0
        running = true
        while(running) {
            try {
                //println "$name waiting to take an item."
                Object item = input.take()
                //println "$name took an item."
                if (item == Worker.DONE) {
                    println "Sink is finished."
                    running = false
                }
                else {
                    store(item)
                    ++count
                    if (total > 0 && count >= total) {
                        running = false
                    }
                }
            }
            catch (InterruptedException e) {
                running = false
                Thread.currentThread().interrupt()
            }
        }
        println "Sink $name terminated."
    }

    abstract void store(Object item)
}
