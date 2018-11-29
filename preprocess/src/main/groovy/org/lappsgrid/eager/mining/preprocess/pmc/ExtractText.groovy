package org.lappsgrid.eager.mining.preprocess.pmc

import com.codahale.metrics.Meter

import groovy.io.FileType
import groovy.cli.picocli.*
import groovy.util.logging.Slf4j
import org.apache.commons.pool2.ObjectPool
import org.apache.commons.pool2.impl.GenericObjectPool
import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.core.Utils
import org.lappsgrid.eager.core.jmx.Registry
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.eager.mining.preprocess.jmx.Manager
import org.lappsgrid.eager.mining.preprocess.jmx.ManagerMBean
import org.lappsgrid.eager.mining.preprocess.pool.ParserFactory
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.lif.Container

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

import static org.lappsgrid.discriminator.Discriminators.*

/**
 *
 */
@Slf4j("logger")
class ExtractText implements Runnable {

    static final String MAILBOX = "eager.preprocess.parsed"
    Meter documentCount
    Meter documentError
    Meter nlpCount
    Meter nlpError
    Meter preprocessError
    Meter preprocessCount

//    Timer documentTimer

    File input
    File output
    boolean enableJMX = false
    int nThreads = 2
    int limit = -1
    AtomicInteger received = new AtomicInteger(0)
    Object semaphore = new Object()

    List<String> stopwords
    boolean running

    void terminate() {
        running = false
    }

    void run() {
        Configuration c = new Configuration()
        if (!input.exists()) {
            logger.error("Input directory not found: {}", input.path)
            return
        }
        if (!output.exists()) {
            logger.error("Output directory not found: {}", output.path)
            return
        }
        if (enableJMX) {
            documentCount = Registry.meter('preprocess.document.count')
            documentError = Registry.meter("preprocess.document.errors")
            preprocessCount = Registry.meter("preprocessed.parsed.count")
            preprocessError = Registry.meter("preprocessed.parsed.errors")
            nlpCount = Registry.meter("preprocessed.nlp.count")
            nlpError = Registry.meter("preprocessed.nlp.errors")
//            documentTimer = Registry.timer("preprocess.timer")
            Registry.register(new Manager(this), "org.lappsgrid.eager.mining.preprocess.pmc.ExtractText:type=ExtractText")
            Registry.startJmxReporter()
            Registry.startLogReporter("org.lappsgrid.eager.mining.metrics", 5, TimeUnit.MINUTES)
        }
        loadStopWords()

        ExecutorService executor = Executors.newFixedThreadPool(nThreads)
        int sent = 0
        running = true
        int count = 0
        long startTime = System.currentTimeMillis()
        ObjectPool<Parser> pool = new GenericObjectPool<Parser>(new ParserFactory())
        Pipeline nlp = new Pipeline()

        DirectoryIterator it = new DirectoryIterator(input)
        File file = it.next()
        while (running && file) {
            ++count
            if (limit > 0 && count > limit) {
                running = false
                break
            }
            logger.info("{} = {}", count, file.path)
            mark(documentCount)
            try {
                Context context = new Context()
                context.parsers = pool
                context.pipeline = nlp
                context.stopwords = stopwords
                context.directory = output.path
                context.text = file.text
                context.parent = file.parentFile.name
                context.filename = file.name
//                String text = parser.parse(file.text)
//                Container container = new Container()
//                container.text = text
//                container.language = "en"
//                Data data = new Data(Uri.LIF, container)
//                Message message = new Message()
//                        .body(data.asJson())
//                        .route(c.BOX_NLP_STANFORD, MAILBOX)
//                        .set("output.dir", output.path)
//                        .set("parent.dir", file.parentFile.name)
//                        .set("file.name", file.name)

                executor.submit(new Processor(this, context))
//                po.send(message)
                ++sent
            }
            catch (Exception e) {
                mark(documentError)
                logger.error("Unable to process {}", file.path, e)
            }
            file = it.next()
        }

        logger.info("Waiting for all processing to be complete.")
        while (received.get() < sent) {
            logger.debug("Recv: {} Sent: {}", received.get(), sent)
            synchronized (semaphore) {
                semaphore.wait()
            }
        }
        long elapsed = System.currentTimeMillis() - startTime

        logger.info("Processed {} files in {}", sent, Utils.format(elapsed))
        logger.info("Shutting down.")
        if (enableJMX) {
            Registry.stopJmxReporter()
            Registry.stopLogReporter()
        }

        executor.shutdown()
        try {
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                logger.warn("Forcing a shutdown after 30 seconds.")
                executor.shutdownNow()
            }
        }
        catch (InterruptedException e) {
            logger.warn("Interupted! Forcing the executor to shutdown")
            executor.shutdownNow()
        }
//        po.close()
//        mbox.close()
        logger.info("Preprocessing complete")
    }

    void loadStopWords() {
        stopwords = []
        InputStream stream = this.class.getResourceAsStream('/stopwords-en.txt')
        if (stream == null) {
            logger.error("Unable to load stopwords")
            return
        }
        stream.text.eachLine { stopwords << it }
        logger.info("Loaded {} stop words", stopwords.size())
    }

    void preprocessorError() {
        mark(preprocessError)
        completed()
    }

    void preprocessorMark() {
        mark(preprocessCount)
        completed()
    }

    void mark(Meter meter) {
        if (meter) meter.mark()
    }

    void completed() {
        int n = received.incrementAndGet()
        logger.trace("Comleted #{}", n)
        synchronized (semaphore) {
            semaphore.notifyAll()
        }
    }

    static void main(String[] args) {
        CliBuilder cli = new CliBuilder(name:'preprocess')

        cli.i(longOpt:'input', args:1, argName: 'file', 'input directory')
        cli.o(longOpt:'output', args:1, argName: 'file', 'output directory')
        cli.j(longOpt:'jmx', 'enable JMX')
        cli.t(longOpt:'threads', args:1, argName: 'N', 'number of worker threads to start')
        cli.l(longOpt:'limit', args:1, argName:'N', 'maximum number of file to process')
        cli.h(longOpt:"help", 'display this help message')
        cli.usageMessage.with {
            headerHeading("%n@|bold Description|@%n")
            header("Extract the text from PubMed Central XML files.")
            synopsisHeading("%n@|bold Synopsis|@%n")
            footerHeading("%n@|bold Notes|@%n")
            footer("the @|bold --input|@ and @|bold --output|@ must be the names/paths to already existing directories.")
        }
        OptionAccessor params = cli.parse(args)
        if (params == null) {
            return
        }

        if (params.h) {
            cli.usage()
            return
        }
        if (!params.i && !params.o) {
            println "error: Missing required options [--input=<file>, --output=<file>]\n"
            cli.usage()
            return
        }
        if (!params.i) {
            println "error: Missing required option [--input=<file>]\n"
            cli.usage()
            return
        }
        if (!params.o) {
            println "error: Missing required option [--output=<file>]\n"
            cli.usage()
            return
        }
        ExtractText app = new ExtractText()
        app.input = new File(params.i)
        app.output = new File(params.o)
        if (params.t) {
            app.nThreads = params.t as int
        }
        if (params.l) {
            app.limit = params.l as int
        }
        app.enableJMX = params.j
        app.run()
    }
}
