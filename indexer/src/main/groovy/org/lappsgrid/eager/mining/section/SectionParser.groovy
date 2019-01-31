package org.lappsgrid.eager.mining.section

import com.codahale.metrics.Meter
import com.codahale.metrics.Timer
import org.lappsgrid.eager.mining.core.Factory
import org.lappsgrid.eager.mining.api.Worker

import java.util.concurrent.BlockingQueue

import static org.lappsgrid.eager.mining.section.Main.metrics
import static org.lappsgrid.eager.mining.section.Main.name

/**
 *
 */
//@Log4j2
class SectionParser extends Worker {
//    static final Logger logger = LoggerFactory.getLogger(SectionParser)

    /** Stats about requests made to this service. */
    final Meter requests
    final Meter badRequests

    /** Used to time each request. */
    final Timer timer

    XmlParser parser
    long count

    SectionParser(BlockingQueue<Packet> input, BlockingQueue<Packet> output) {
        super("SectionParser", input, output)
        parser = Factory.createXmlParser()
        count = 0

        requests = metrics.meter(name("parser", "requests"))
        badRequests = metrics.meter(name("parser", "bad-requests"))
        timer = metrics.timer(name("parser", "timer"))
    }

    @Override
    Object work(Object item) {
        if (! (item instanceof Packet)) {
            //logger.error("received data that was not a Packet")
            badRequests.mark()
            return null
        }
        requests.mark()
        Timer.Context context = timer.time()
        try {
            ++count
            Packet packet = (Packet) item
            String xml = packet.asString()
            //logger.info("count: {} size: {}", count,xml.length())
            Node node = parser.parseText(xml)
            packet.node(node)
            return packet
        }
        finally {
            context.stop()
        }
    }
}
