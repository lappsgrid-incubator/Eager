package org.lappsgrid.eager.mining.solr

import org.lappsgrid.eager.mining.solr.api.Haltable
import org.lappsgrid.eager.mining.solr.api.Sink

import java.text.SimpleDateFormat
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 *  Entry point for the command line indexer program.
 */
class Indexer {

    File directory

    void run() {
        BlockingQueue<Object> files = new ArrayBlockingQueue<>(10)
        BlockingQueue<Object> documents = new ArrayBlockingQueue<>(10)

        boolean save = false
        Sink collector
        List<Haltable> threads = []
//        if (worker == 'parser') {
            threads << new Parser(1, files, documents)
            collector = new SolrInserter(documents)
            threads << collector
//        }
//        else {
//            save = true
//            threads << new IDParser(1, files, documents)
//            threads << new IDParser(2, files, documents)
//            threads << new IDParser(3, files, documents)
//            threads << new IDParser(4, files, documents)
//            collector = new IDCollector(documents)
//            threads << collector
//        }

        Haltable lister = new DirectoryLister(directory, collector, files)
        threads << lister

        long startTime = System.currentTimeMillis()

        // Start all the threads
        threads*.start()

        // When the collector has terminated it is safe to shutdown the
        // remaining threads.
        collector.join()
        threads.each { it.halt() }
        if (save) {
            IDCollector ids = (IDCollector) collector
            ids.save(new File('pmc-index.csv'))
        }
        long duration = System.currentTimeMillis() - startTime
        println "All threads have terminated."
        println "Running time: ${(new SimpleDateFormat("hh:mm:ss").format(new Date(duration)))}"

    }

    static void main(String[] args) {
        if (args.size() != 1) {
            println "USAGE: java -jar solr-indexr.jar /directory/to/index"
            return
        }
        File file = new File(args[0])
        if (!file.exists()) {
            println "ERROR: Input directory not found."
            return
        }
        Indexer app = new Indexer()
        app.directory = file
//        app.worker = args[1]
        app.run()
    }
}
