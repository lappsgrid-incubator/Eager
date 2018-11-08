package org.lappsgrid.eager.mining.api

import groovy.util.logging.Slf4j

import java.util.concurrent.BlockingQueue

/**
 * A Sink takes objects from its input queue and stores them somewhere.
 */
@Slf4j("logger")
abstract class Sink extends Haltable {
//    static final Logger logger = LoggerFactory.getLogger(Sink)
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
        logger.info("Starting sink {}", name)
        int count = 0
        running = true
        while(running) {
            try {
                //println "$name waiting to take an item."
                Object item = input.take()
                //println "$name took an item."
                if (item == Worker.DONE) {
                    logger.info("Sink is finished.")
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
        finish()
        logger.info("Sink {} terminated.", name)
    }

    abstract void store(Object item)

    /**
     * Subclasses can overide this method to perform any clean up that is required.
     */
    void finish() { }
}
