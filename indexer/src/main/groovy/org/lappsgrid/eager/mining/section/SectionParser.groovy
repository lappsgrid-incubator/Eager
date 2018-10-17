package org.lappsgrid.eager.mining.section

import com.codahale.metrics.*
import org.lappsgrid.eager.core.Factory
import org.lappsgrid.eager.mining.api.Worker

import java.util.concurrent.BlockingQueue

import static org.lappsgrid.eager.mining.section.Main.metrics

/**
 *
 */
class SectionParser extends Worker {

    /** Stats about requests made to this service. */
    final Meter requests
    /** Used to time each request. */
    final Timer timer

    XmlParser parser
    long count

    SectionParser(BlockingQueue<Object> input, BlockingQueue<Object> output) {
        super("SectionParser", input, output)
        parser = Factory.createXmlParser()
        count = 0

        requests = metrics.meter("org.lappsgrid.eager.mining.parser.requests")
        timer = metrics.timer("org.lappsgrid.eager.mining.parser.timer")
    }

    @Override
    Object work(Object item) {
        if (! (item instanceof String)) {
            return null
        }
        requests.mark()
        Timer.Context context = timer.time()
        try {
            ++count
            String xml = item.toString()
            println "SectionParser.work: $count size: ${xml.length()}"
            return parser.parseText(xml)
        }
        finally {
            context.stop()
        }
    }
}
