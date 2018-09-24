package org.lappsgrid.eager.mining.solr

import org.lappsgrid.eager.mining.solr.api.DirectoryLister
import org.lappsgrid.eager.mining.solr.api.Sink

import java.util.concurrent.BlockingQueue

/**
 *
 */
class PubmedDirectoryLister extends DirectoryLister {
    PubmedDirectoryLister(File directory, Sink sink, BlockingQueue<Object> output) {
        super(directory, sink, output)
    }

    @Override
    String suffix() {
        return ".xml"
    }
}
