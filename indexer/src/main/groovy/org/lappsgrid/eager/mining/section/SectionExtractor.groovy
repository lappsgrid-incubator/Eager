package org.lappsgrid.eager.mining.section

import com.codahale.metrics.*
import org.lappsgrid.eager.mining.api.Worker

import java.util.concurrent.BlockingQueue
import static Main.metrics

/**
 *
 */
class SectionExtractor extends Worker {

    final Meter requests
    final Timer timer

    SectionExtractor(BlockingQueue<Node> input, BlockingQueue<Set<String>> output) {
        super("FileLoader", input, output)
        requests = metrics.meter("org.lappsgrid.eager.mining.extractor.requests")
        timer = metrics.timer("org.lappsgrid.eager.mining.extractor.timer")
    }

    @Override
    Object work(Object item) {
        if (! (item instanceof Node)) {
            println "Invalid object sent to SectionExtractor: ${item}"
            return null
        }

        requests.mark()
        Timer.Context context = timer.time()
        try {
            Set<String> sections = new HashSet<>()
            Node article = (Node) item
            article.body.sec.each { Node section ->
                String type = section.attribute('sec-type')
                if (type) {
                    type = type.trim().toLowerCase()
                    sections.add(type)
                }
            }
            println "SectionExtractor.work: extracted ${sections.size()} sections"
            return sections
        }
        catch (Exception e) {
            e.printStackTrace()
        }
        finally {
            context.stop()
        }
    }
}
