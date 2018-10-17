package org.lappsgrid.eager.mining.section

import org.lappsgrid.eager.mining.api.Haltable
import org.lappsgrid.eager.mining.api.Worker
import org.lappsgrid.eager.mining.jmx.Manager
import org.lappsgrid.eager.mining.jmx.SizeGauge

import javax.management.MBeanServer
import javax.management.ObjectName
import java.lang.management.ManagementFactory
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

import com.codahale.metrics.*

import java.util.concurrent.TimeUnit

/**
 *
 */
class Main {
    static final String MANAGER_NAME = "org.lappsgrid.eager.mining:type=Manager"
    public static final MetricRegistry metrics = new MetricRegistry()

    File source
    File target

    void run() {

        long startTime = System.currentTimeMillis()

        BlockingQueue<Packet> files = new ArrayBlockingQueue<>(1024)
        BlockingQueue<Packet> nodes = new ArrayBlockingQueue<>(1024)
        BlockingQueue<Packet> sections = new ArrayBlockingQueue<>(1024)

        MBeanServer server = ManagementFactory.getPlatformMBeanServer()
        Manager manager = new Manager()
                .filesQ(files)
                .nodesQ(nodes)
                .sectionsQ(sections)
        server.registerMBean(manager, new ObjectName(MANAGER_NAME))

        metrics.register(name("files", "size"), new SizeGauge(files))
        metrics.register(name("nodes","size"), new SizeGauge(nodes))
        metrics.register(name("sections","size"), new SizeGauge(sections))

        JmxReporter jmx = JmxReporter.forRegistry(metrics).build()
        jmx.start()

        ConsoleReporter console = ConsoleReporter.forRegistry(metrics)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .build()
        console.start(10, TimeUnit.SECONDS)

        Worker parser = new SectionParser(files, nodes)
        Worker extractor = new SectionExtractor(nodes, sections)
        SectionSink sink = new SectionSink(sections)
//        DirectoryLister lister = new PmcDirectoryLister(source, sink, files)
        RemoteDocumentProvider lister = new RemoteDocumentProvider(sink, files)

        List<Haltable> threads = [ parser, extractor, sink, lister ]

        // Start all the threads
        threads.each { it.start() }

        // Wait on the sink. When it is done the pipeline has completed.
        sink.join()
        sink.save(target)

        // Shut everything down.
        threads.each { it.halt() }
        jmx.stop()
        console.stop()

        long elapsed = System.currentTimeMillis() - startTime

//        File parent = target.parentFile
//        File stats = new File(parent, "stats.txt")
//        stats.withWriter { writer ->
//            writer.print("Duration: ")
//            writer.println(format(elapsed))
//            writer.println()
//            writer.println("Meters")
//            writer.println("------")
//            metrics.meters.each { String name, Meter meter ->
//                writer.println(String.format("%s\t%6d\t%3.3d\t%3.3d\t%3.3d\t%3.3d", name, meter.count, meter.meanRate, meter.oneMinuteRate, meter.fiveMinuteRate, meter.fifteenMinuteRate))
//            }
//            metrics.timers.each { String name, Timer timer ->
//            }
//        }
        println "Section collection finished in " + format(elapsed)
    }

    static String name(String... parts) {
        return "org.lappsgrid.eager.mining." + parts.join(".")
    }

    String format(long input) {
        long msec = input % 1000
        long seconds = input / 1000
        long minutes = seconds / 60
        seconds = seconds % 60
        long hours = minutes / 60
        minutes = minutes % 60
        return String.format("%d:%02d:%02d.%03d", hours, minutes, seconds, msec);
    }

    static void main(String[] args) {
        if (args.length != 1) {
            String name = Main.class.name
//            println "USAGE: java -cp *.jar org.lappsgrid.eager.mining.solr.section.Main <input directory> <output filename>"
            println "USAGE: java -cp *.jar $name <output filename>"
            return
        }
        Main app = new Main()
//        app.source = new File(args[0])
//        if (!app.source.exists()) {
//            println "Input directory not found."
//            return
//        }
//        if (!app.source.isDirectory()) {
//            println "Input ${app.source.path} is not a directory."
//            return
//        }
        app.target = new File(args[0])
        app.run()
    }
}
