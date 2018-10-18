package org.lappsgrid.eager.mining.section

import com.codahale.metrics.*
import com.codahale.metrics.Counter as MetricsCounter

import groovy.util.logging.Slf4j
import org.lappsgrid.eager.mining.api.Sink

import java.util.concurrent.BlockingQueue

import static Main.metrics
import static Main.name

/**
 *
 */
@Slf4j('logger')
class SectionSink extends Sink {

    final Meter requests
//    final Meter badRequests
    final Timer timer
    final MetricsCounter badRequests
    final MetricsCounter emptyFiles

    Set<String> sections
    Map<String,Counter> counters
//    List<String> badFiles

    SectionSink(BlockingQueue<Packet> input) {
        super("SectionSink", input)
        sections = new HashSet<>()
        counters = new HashMap<>()
//        badFiles = new ArrayList<>()

        requests = metrics.meter(name("sink", "requests"))
        timer = metrics.timer(name("sink", "timer"))
        badRequests = metrics.counter(name("sink", "bad-requests"))
        emptyFiles = metrics.counter(name("sink", "empty-files"))
    }

    @Override
    void store(Object item) {
        if (!(item instanceof Packet)) {
            logger.error("Invalid item : {}", item)
            badRequests.inc()
            return
        }

        Packet packet = (Packet) item
        requests.mark()
        Timer.Context context = timer.time()

        try {
            Set<String> set = packet.asSet()
            if (set.size() == 0) {
                logger.warn("No sections found in: {}", packet.path)
                emptyFiles.inc()
//                badFiles.add(packet.path)
            }
            else {
                logger.info("Saving {} items", set.size())
                set.each { String section ->
                    Counter counter = counters[section]
                    if (counter == null) {
                        counter = new Counter()
                        counters[section] = counter
                    }
                    ++counter
                }
            }
        }
        finally {
            context.stop()
        }
    }

    void save(File destination) {
        logger.info("Writing {} items to {}", counters.size(), destination.path)
        destination.withWriter { writer ->
            Closure comp = { a,b ->
                b.value.count <=> a.value.count
            }
            writer.println("SECTIONS")
            writer.println("Count: ${counters.size()}")
            counters.sort(comp).each { section, count ->
                writer.println("$count\t$section")
            }
            writer.println("FILES WITH NO SECTIONS")
            writer.println("Count: ${badFiles.size()}")
//            badFiles.each { writer.println(it) }
        }
    }
}
