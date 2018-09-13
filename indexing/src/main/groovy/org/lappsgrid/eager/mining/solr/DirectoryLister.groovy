package org.lappsgrid.eager.mining.solr

import org.lappsgrid.eager.mining.solr.api.Source
import org.lappsgrid.eager.mining.solr.api.Sink
import org.lappsgrid.eager.mining.solr.api.Worker

import java.util.concurrent.BlockingQueue

/**
 * Traverse a directory heirrarchy
 */
class DirectoryLister extends Source {

    private Stack<File> stack
    private int count

    public DirectoryLister(File directory, Sink sink, BlockingQueue<Object> output) {
        super("DirectoryLister", sink, output)
        stack = new ArrayDeque<File>()
        directory.listFiles().each { stack.push(it) }
        this.count = 0
    }

    Object produce() {
        while (true) {
            if (stack.isEmpty()) {
                println "Directory lister is done."
                return Worker.DONE
            }
            File item = stack.pop()
            if (item.isFile()) {
                if (item.name.endsWith('.nxml')) {
                    println "${++count} DirectortLister queueing ${item.name}"
                    return item
                }
            }
            else {
                item.listFiles().each { stack.push(it) }
            }
        }
    }
}
/*
class DirectoryLister extends Haltable {

    public static final File DONE = new File("");

    BlockingQueue<File> q
    File directory
    Stack<File> stack;

    DirectoryLister(BlockingQueue q, File directory) {
        this.q = q
        this.directory = directory
        stack = new ArrayDeque<>()
    }

    void run() {
        println "Directory listing thread starting."
        running = true
        stack.push(directory)
        while (running && stack.size() > 0) {
            File current = stack.pop()
            if (current.isFile()) {
                try {
                    if (current.name.endsWith('.nxml')) {
                        q.put(current)
                    }
                }
                catch (InterruptedException e) {
                    println "Directory listing thread has been interrupted."
                    running = false
                    Thread.currentThread().interrupt()
                }
            }
            else {
                current.listFiles().each { stack.push(it) }
            }
        }
        q.put(DONE)
        q.put(DONE)
        println "Directory listing thread has terminated."
    }
}
*/