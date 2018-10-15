package org.lappsgrid.eager.mining.solr.api

import org.lappsgrid.eager.mining.solr.api.Source
import org.lappsgrid.eager.mining.solr.api.Sink
import org.lappsgrid.eager.mining.solr.api.Worker

import java.util.concurrent.BlockingQueue

/**
 * Traverse a directory hierarchy
 */
abstract class DirectoryLister extends Source {

    Stack<File> stack
    int count

    public DirectoryLister(File directory, Sink sink, BlockingQueue<Object> output) {
        super("DirectoryLister", sink, output)
        stack = new ArrayDeque<File>()
        directory.listFiles().each { stack.push(it) }
        this.count = 0
    }

    abstract String suffix()

    Object produce() {
        while (true) {
            if (stack.isEmpty()) {
                println "Directory lister is done."
                return Worker.DONE
            }
            File item = stack.pop()
            if (item.isFile()) {
                if (item.name.endsWith(suffix())) {
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
