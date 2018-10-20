package org.lappsgrid.eager.mining.api

import java.util.concurrent.BlockingQueue

//import groovy.util.logging.Slf4j
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
/**
 * A Source object provides data and adds it to its output queue.  If a <tt>Sink</tt> is supplied
 * it will be notified of how many objects were placed on the output queue when the <tt>Source</tt>
 * has finished generating objects.
 */
//@Log4j2
abstract class Source extends Haltable {

//    static final Logger logger = LoggerFactory.getLogger(Source)
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
        //logger.info("Source {} starting", name)
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
            //logger.info("Sent notification {} to {}", count, sink.name)
            sink.total = count
        }
        //logger.info("Source {} terminated", name)
    }
}
