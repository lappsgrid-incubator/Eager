package org.lappsgrid.eager.mining.section

import com.codahale.metrics.Meter
import com.codahale.metrics.Timer
import org.lappsgrid.eager.mining.api.Worker

import java.util.concurrent.BlockingQueue
import static org.lappsgrid.eager.mining.section.Main.metrics
import static org.lappsgrid.eager.mining.section.Main.name

/**
 *
 */
//@Log4j2
class SectionExtractor extends Worker {
//    static final Logger logger = LoggerFactory.getLogger(SectionExtractor)
    final Meter requests
    final Meter badRequests
    final Timer timer

    SectionExtractor(BlockingQueue<Packet> input, BlockingQueue<Packet> output) {
        super("FileLoader", input, output)
        requests = metrics.meter(name("extractor", "requests"))
        timer = metrics.timer(name("extractor", "timer"))
    }

    @Override
    Object work(Object item) {
        if (! (item instanceof Packet)) {
            //logger.error("Invalid object sent to SectionExtractor: {}", item)
            badRequests.mark()
            return null
        }
        Packet packet = (Packet) item
        requests.mark()
        Timer.Context context = timer.time()
        try {
            Set<String> sections = new HashSet<>()
            Node article = packet.asNode()
            article.body.sec.each { Node section ->
                String type = section.attribute('sec-type')
                if (type) {
                    type = type.trim().toLowerCase()
                    sections.add(type)
                }
            }
            //logger.info("extracted {} sections", sections.size())
            packet.set(sections)
            return packet
        }
        catch (Exception e) {
            //logger.error("Unable to extract sections", e)
        }
        finally {
            context.stop()
        }
    }
}
