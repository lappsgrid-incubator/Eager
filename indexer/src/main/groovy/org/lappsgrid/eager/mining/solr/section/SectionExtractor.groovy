package org.lappsgrid.eager.mining.solr.section

import org.lappsgrid.eager.mining.solr.api.Worker

import java.util.concurrent.BlockingQueue

/**
 *
 */
class SectionExtractor extends Worker {

    SectionExtractor(BlockingQueue<Node> input, BlockingQueue<Set<String>> output) {
        super("FileLoader", input, output)
    }

    @Override
    Object work(Object item) {
        if (! (item instanceof Node)) {
            println "Invalid object sent to SectionExtractor: ${item}"
            return null
        }
        Set<String> sections = new HashSet<>()
        Node article = (Node) item
        article.body.sec.each { Node section ->
            sections.add(section.attribute("sec-type"))
        }
        println "Found ${sections.size()} section types"
        return sections
    }
}
