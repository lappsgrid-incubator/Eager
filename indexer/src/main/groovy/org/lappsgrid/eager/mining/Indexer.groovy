package org.lappsgrid.eager.mining

import com.codahale.metrics.Timer
import org.lappsgrid.eager.mining.core.Utils
import org.lappsgrid.eager.mining.core.jmx.Registry
import org.lappsgrid.eager.mining.api.Haltable
import org.lappsgrid.eager.mining.api.Sink
import org.lappsgrid.eager.mining.api.Source
import org.lappsgrid.eager.mining.io.DirectoryLister
import org.lappsgrid.eager.mining.io.IndexLister
import org.lappsgrid.eager.mining.io.Lister
import org.lappsgrid.eager.mining.io.PmcDirectoryLister
import org.lappsgrid.eager.mining.io.PubmedDirectoryLister
import org.lappsgrid.eager.mining.jmx.Manager
import org.lappsgrid.eager.mining.parser.PMCExtractor
import org.lappsgrid.eager.mining.parser.Parser
import org.lappsgrid.eager.mining.parser.PubmedExtractor
import org.lappsgrid.eager.mining.parser.XmlDocumentExtractor
import org.lappsgrid.eager.mining.solr.SolrInserter

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

import groovy.cli.picocli.*

import java.util.concurrent.TimeUnit

/**
 *  Entry point for the command line indexer program.
 */
class Indexer {

    final Timer timer

    File directory
//    XmlDocumentExtractor extractor
    Closure extractorFactory
    Closure listerFactory
    String collection
    int nParsers

    Indexer() {
        timer = Registry.timer()
        nParsers = 1
    }

    void run() {

        BlockingQueue<Object> files = new ArrayBlockingQueue<>(10)
        BlockingQueue<Object> documents = new ArrayBlockingQueue<>(10)

//        boolean save = false
//        Parser parser1 = new Parser(1, files, documents, extractorFactory())
//        Parser parser2 = new Parser(2, files, documents, extractorFactory())
//        Parser parser3 = new Parser(3, files, documents, extractorFactory())
//        Parser parser4 = new Parser(4, files, documents, extractorFactory())

        // Create the specified number of parser threads.
        List<Haltable> threads = []
        nParsers.times { i ->
            threads.add(new Parser(i, files, documents, extractorFactory()))
        }

        // The last step in the pipeline is to index the document with Solr.
        Sink collector = new SolrInserter(collection, documents)
        Lister lister = listerFactory(directory, collector, files)
        threads.add(collector)
        threads.add((Source)lister)

        // Start the JMX manager and reporters.
        Manager manager = new Manager(lister)
        Registry.register(manager, "org.lappsgrid.eager.mining.Indexer:type=Indexer")
        Registry.startJmxReporter()
        Registry.startLogReporter("org.lappsgrid.eager.mining.metrics", 5, TimeUnit.MINUTES)


        // Record the start time and start all the threads.
        long startTime = System.currentTimeMillis()
        threads*.start()

        // When the collector has terminated it is safe to shutdown the
        // remaining threads.
        collector.join()
        threads.each { it.halt() }
        threads*.join()
        long duration = System.currentTimeMillis() - startTime
        Registry.stopLogReporter()
        Registry.stopJmxReporter()
        println "All threads have terminated."
        println "Running time: ${format(duration)}"
    }

    static String format(long input) {
//        long msec = input % 1000
//        long seconds = input / 1000
//        long minutes = seconds / 60
//        seconds = seconds % 60
//        long hours = minutes / 60
//        minutes = minutes % 60
//        return String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, msec);
        return Utils.format(input)
    }

    static void main(String[] args) {
        CliBuilder cli = new CliBuilder()
        cli.pubmed('index PubMed baseline files')
        cli.pmc('index PubMed Central files')
        cli.c(longOpt: "collection", args:1, argName:"CORE","collection to add documents to")
        cli.i(longOpt:'indexed', 'use the PMC index to retrieve documents')
        cli.r(longOpt:'random', 'extract a random sample from PMC')
        cli.s(longOpt:'size', args:1, argName: 'NUM', 'sample size')
        cli.p(longOpt: 'parsers', args:1, argName: 'NUM', 'the number of parser threads to spawn')
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
        if (files.size() == 0 && !params.i) {
            println "No input file/directory was given."
            cli.usage();
            return;
        }
        if (files.size() > 1) {
            println "More that one directory was specified.  Only the first one will be processed."
        }
        File file = null
        if (files.size() > 0) {
            file = new File(files.get(0))
        }
        String collection = "eager"
        if (params.c) {
            collection = params.c
        }
        int nThreads = 1
        if (params.p) {
            nThreads = params.p as int
        }
//        XmlDocumentExtractor extractor = null
        Closure extractor = null
        Closure lister = null
        if (params.pubmed && params.pmc) {
            println "ERROR: Only one of -pubmed or -pmc can be specified."
            cli.usage();
            return;
        }
        else if (params.pubmed) {
            extractor = { new PubmedExtractor() }
            lister = { dir, sink, Q -> new PubmedDirectoryLister(dir, sink, Q) }
        }
        else if (params.pmc) {
            extractor = { new PMCExtractor() }
            lister = { dir, sink, Q -> new PmcDirectoryLister(dir, sink, Q) }
        }
        else if (params.i) {
            extractor = { new PMCExtractor() }
            int size = -1
            if (params.s) {
                size = params.s as int
            }
            lister = { dir, sink, Q -> new IndexLister(sink, Q, size)}
        }
        else {
            println "ERROR: One of -pubmed or -pmc or -indexed is required."
            cli.usage()
            return
        }

        Indexer app = new Indexer()
        app.directory = file
        app.extractorFactory = extractor
        app.listerFactory = lister
        app.collection = collection
        app.run()
    }
}
