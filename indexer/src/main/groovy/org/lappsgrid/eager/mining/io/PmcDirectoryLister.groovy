package org.lappsgrid.eager.mining.io

import org.lappsgrid.eager.mining.api.Sink

import java.util.concurrent.BlockingQueue

/**
 *
 */
class PmcDirectoryLister extends DirectoryLister {
    PmcDirectoryLister(File directory, Sink sink, BlockingQueue<Object> output) {
        super(directory, sink, output)
    }

    @Override
    String suffix() {
        return ".nxml"
    }
}
