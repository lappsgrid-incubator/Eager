package org.lappsgrid.eager.mining.solr.section

import org.lappsgrid.eager.core.Factory
import org.lappsgrid.eager.mining.solr.api.Worker

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 *
 */
class SectionParser extends Worker {

    XmlParser parser
    long count

    SectionParser(BlockingQueue<Object> input, BlockingQueue<Object> output) {
        super("SectionParser", input, output)
        parser = Factory.createXmlParser()
        count = 0
    }

    @Override
    Object work(Object item) {
        if (! (item instanceof File)) {
            return null
        }
        ++count
        printf "%07d Parsing %s\n", count, item.path
        return parser.parse((File) item)
    }
}
