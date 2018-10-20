package org.lappsgrid.eager.mining.solr.unused

import org.lappsgrid.eager.mining.api.Sink

import java.util.concurrent.BlockingQueue

/**
 *
 */
class IDCollector extends Sink {

    List<Values> values
    public IDCollector(BlockingQueue<Object> input) {
        super("IDCollector", input)
        values = new ArrayList<>()
    }

    void store(Object object) {
        values.add((Values) object)
    }

    void save(File file) {
        save(new FileWriter(file))
    }

    void save(Writer writer) {
        PrintWriter out
        if (writer instanceof PrintWriter) {
            out = (PrintWriter) writer
        }
        else {
            out = new PrintWriter(writer)
        }
        values.each {
            out.println( it.toString() )
        }
        out.flush()
        out.close()
    }
}
