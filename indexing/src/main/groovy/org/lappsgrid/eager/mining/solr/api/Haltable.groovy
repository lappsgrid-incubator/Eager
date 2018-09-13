package org.lappsgrid.eager.mining.solr.api

/**
 * The Haltable class maintains the running flag and provides a halt method that
 * sets the flag to false.  Threads that extend Haltable are expected to set the running
 * flag to true when they start processing and then monitor the running flag and terminate
 * when it changes to false.
 */
abstract class Haltable implements Runnable {
    protected boolean running
    protected Thread thread;

    Thread start() {
        thread = new Thread(this)
        thread.start()
        return thread
    }

    void stop() {
        halt()
    }

    void join() {
        if (thread == null) {
            return
        }
        thread.join()
    }

    void halt() {
        if (thread == null) {
            return
        }
        running = false
        thread.interrupt()
//        Thread.currentThread().interrupt()
    }
}
