package org.lappsgrid.eager.mining.solr.api

import java.util.concurrent.BlockingQueue

/**
 * A Source object provides data and adds it to its output queue.  If a <tt>Sink</tt> is supplied
 * it will be notified of how many objects were placed on the output queue when the <tt>Source</tt>
 * has finished generating objects.
 */
abstract class Source extends Haltable {

    String name
    BlockingQueue<Object> output
    Sink sink

    public Source(String name, BlockingQueue<Object> output) {
        this(name, null, output)
    }

    public Source(String name, Sink sink, BlockingQueue<Object> output) {
        this.name = name
        this.sink = sink
        this.output = output
    }

    abstract Object produce()

    @Override
    void run() {
        println "Source $name starting"
        int count = 0
        running = true
        while (running) {
            try {
                Object item = produce()
                if (item == Worker.DONE) {
                    running = false
                }
                else {
                    output.put(item)
                    ++count
                }
            }
            catch (InterruptedException e) {
                running = false
                Thread.currentThread().interrupt()
            }
        }
        // Tell the sink how many items it should expect.
        if (sink != null) {
            println "Sent notification $count to ${sink.name}"
            sink.total = count
        }
        println "Source $name terminated"
    }
}
