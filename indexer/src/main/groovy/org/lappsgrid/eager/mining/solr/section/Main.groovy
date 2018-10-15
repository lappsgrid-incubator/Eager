package org.lappsgrid.eager.mining.solr.section

import org.lappsgrid.eager.mining.solr.PmcDirectoryLister
import org.lappsgrid.eager.mining.solr.api.DirectoryLister
import org.lappsgrid.eager.mining.solr.api.Haltable
import org.lappsgrid.eager.mining.solr.api.Sink
import org.lappsgrid.eager.mining.solr.api.Worker

import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

/**
 *
 */
class Main {

    File source
    File target

    void run() {
//        File source = new File("src/test/resources/")
        BlockingQueue<File> files = new ArrayBlockingQueue<>(1024)
        BlockingQueue<Node> nodes = new ArrayBlockingQueue<>(1024)
        BlockingQueue<Set<String>> sections = new ArrayBlockingQueue<>(1024)

        Worker parser = new SectionParser(files, nodes)
        Worker extractor = new SectionExtractor(nodes, sections)
        SectionSink sink = new SectionSink(sections)
        DirectoryLister lister = new PmcDirectoryLister(source, sink, files)

        List<Haltable> threads = [ parser, extractor, sink, lister ]
//        threads.add(parser.start())
//        threads.add(extractor.start())
//        threads.add(sink.start())
//        threads.add(lister.start())

        threads.each { it.start() }
        sink.join()
        sink.save(target)
        threads.each { it.halt() }
        println "Section collection finished"
    }

    static void main(String[] args) {
        if (args.length != 2) {
            println "USAGE: java -cp *.jar org.lappsgrid.eager.mining.solr.section.Main <input directory> <output filename>"
            return
        }
        Main app = new Main()
        app.source = new File(args[0])
        if (!app.source.exists()) {
            println "Input directory not found."
            return
        }
        if (!app.source.isDirectory()) {
            println "Input ${app.source.path} is not a directory."
            return
        }
        app.target = new File(args[1])
        app.run()
    }
}
