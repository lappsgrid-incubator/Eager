package org.lappsgrid.eager.mining.solr.section

import org.lappsgrid.eager.mining.solr.api.Sink

import java.util.concurrent.BlockingQueue

/**
 *
 */
class SectionSink extends Sink {

    Set<String> sections

    SectionSink(BlockingQueue<Set<String>> input) {
        super("SectionSink", input)
        sections = new HashSet<>()
    }

    @Override
    void store(Object item) {
        if (!(item instanceof Set)) {
            println "Invalid into to SectionSink: ${item}"
            return
        }
//        println "Adding ${((Set)item).size()} section names"
        sections.addAll((Set) item)
    }

    void save(File destination) {
        println "Writing ${destination.path}"
        destination.withWriter { writer ->
            sections.sort().each { section ->
                writer.println(section)
            }
        }
    }
}
