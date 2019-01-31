package org.lappsgrid.eager.mining.io

import com.codahale.metrics.Meter
import com.codahale.metrics.Timer
import groovy.util.logging.Slf4j
import org.lappsgrid.eager.mining.core.jmx.Registry
import org.lappsgrid.eager.mining.api.Sink
import org.lappsgrid.eager.mining.api.Source
import org.lappsgrid.eager.mining.api.Worker

import java.util.concurrent.BlockingQueue

/**
 * Traverse a directory hierarchy
 */
@Slf4j("logger")
abstract class DirectoryLister extends Source implements Lister {

    Stack<File> stack
    int count

    final Meter documents = Registry.meter("lister.documents")
    final Timer timer = Registry.timer("lister.timer")

    boolean terminated

    public DirectoryLister(File directory, Sink sink, BlockingQueue<Object> output) {
        super("DirectoryLister", sink, output)
        stack = new ArrayDeque<File>()
        directory.listFiles().each { stack.push(it) }
        this.count = 0
        this.terminated
    }

    abstract String suffix()

    Object produce() {
        if (terminated) {
            return Worker.DONE
        }

        Timer.Context context = timer.time()
        while (true) {
            if (stack.isEmpty()) {
                logger.info("Directory lister is done.")
                context.stop()
                return Worker.DONE
            }
            File item = stack.pop()
            if (item.isFile()) {
                if (item.name.endsWith(suffix())) {
                    documents.mark()
                    context.stop()
                    return item
                }
            }
            else {
                item.listFiles().each { stack.push(it) }
            }
        }
    }

    void terminate() {
        logger.info("Received a terminate message.")
        terminated = true
    }
}
