package org.lappsgrid.eager.mining

import org.lappsgrid.eager.mining.api.Haltable
import org.lappsgrid.eager.mining.api.Sink
import org.lappsgrid.eager.mining.io.PmcDirectoryLister
import org.lappsgrid.eager.mining.io.PubmedDirectoryLister
import org.lappsgrid.eager.mining.parser.PMCExtractor
import org.lappsgrid.eager.mining.parser.PubmedExtractor
import org.lappsgrid.eager.mining.parser.XmlDocumentExtractor
import org.lappsgrid.eager.mining.solr.SolrInserter
import org.lappsgrid.eager.mining.solr.unused.IDCollector

import java.text.SimpleDateFormat
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

import groovy.cli.picocli.*

/**
 *  Entry point for the command line indexer program.
 */
class Indexer {

    File directory
    XmlDocumentExtractor extractor
    Closure listerFactory

    void run() {
        BlockingQueue<Object> files = new ArrayBlockingQueue<>(10)
        BlockingQueue<Object> documents = new ArrayBlockingQueue<>(10)

        boolean save = false
        Sink collector
        List<Haltable> threads = []
//        if (worker == 'parser') {
            threads << new Parser(1, files, documents, extractor)
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

//        Haltable lister = new DirectoryLister(directory, collector, files)
        Haltable lister = listerFactory(directory, collector, files)
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
        CliBuilder cli = new CliBuilder()
        cli.pubmed('index PubMed baseline files')
        cli.pmc('index PubMed Central files')
        cli.h(longOpt:"help", 'display this help message')
        cli.usageMessage.with {
            headerHeading("%n@|bold Description|@%n")
            header("Index files from PubMed or PubMed Central.")
            synopsisHeading("%n@|bold Synopsis|@%n")
            footerHeading("%n@|bold Notes|@%n")
            footer("the @|bold -pubmed|@ and @|bold -pmc|@ options are mutually exclusive.  Only one file type can be processed at a time.")
        }
        OptionAccessor params = cli.parse(args)
        if (params.h) {
            cli.usage()
            return;
        }
        List<String> files = params.arguments()
        if (files.size() == 0) {
            println "No input file/directory was given."
            cli.usage();
            return;
        }
        if (files.size() > 1) {
            println "More that one directory was specified.  Only the first one will be processed."
        }
        File file = new File(files.get(0))
        XmlDocumentExtractor extractor = null
        Closure lister = null
        if (params.pubmed && params.pmc) {
            println "ERROR: Only one of -pubmed or -pmc can be specified."
            cli.usage();
            return;
        }
        else if (params.pubmed) {
            extractor = new PubmedExtractor();
            lister = { dir, sink, Q -> new PubmedDirectoryLister(dir, sink, Q) }
        }
        else if (params.pmc) {
            extractor = new PMCExtractor();
            lister = { dir, sink, Q -> new PmcDirectoryLister(dir, sink, Q) }
        }
        else {
            println "ERROR: One of -pubmed or -pmc is required."
            cli.usage()
            return
        }

        Indexer app = new Indexer()
        app.directory = file
        app.extractor = extractor
        app.listerFactory = lister
        app.run()
    }
}
