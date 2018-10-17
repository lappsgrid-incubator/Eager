package org.lappsgrid.eager.mining.section

import com.codahale.metrics.*
import org.lappsgrid.eager.mining.api.Sink

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

import static Main.metrics
/**
 *
 */
class SectionSink extends Sink {

    final Meter requests
    final Timer timer

    Set<String> sections
    Map<String,Counter> counters

    SectionSink(BlockingQueue<Set<String>> input) {
        super("SectionSink", input)
        sections = new HashSet<>()
        counters = new HashMap<>()

        requests = metrics.meter("org.lappsgrid.eager.mining.sink.requests")
        timer = metrics.timer("org.lappsgrid.eager.mining.sink.timer")
    }

    @Override
    void store(Object item) {
        if (!(item instanceof Set)) {
            println "Invalid into to SectionSink: ${item}"
            return
        }
        requests.mark()
        Timer.Context context = timer.time()

        try {
            Set<String> set = (Set<String>) item
            println "SectionSink.store: Saving ${set.size()} items"
            set.each { String section ->
                Counter counter = counters[section]
                if (counter == null) {
                    counter = new Counter()
                    counters[section] = counter
                }
                ++counter
            }
        }
        finally {
            context.stop()
        }
    }

    void save(File destination) {
        println "Writing ${counters.size()} items to ${destination.path}"
        destination.withWriter { writer ->
            Closure comp = { a,b ->
                b.value.count <=> a.value.count
            }
            counters.sort(comp).each { section, count ->
                writer.println("$count\t$section")
            }
        }
    }
}
