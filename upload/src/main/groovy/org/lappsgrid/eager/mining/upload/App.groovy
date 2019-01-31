package org.lappsgrid.eager.mining.upload

import com.codahale.metrics.Counter
import com.codahale.metrics.Histogram
import com.codahale.metrics.Meter
import com.codahale.metrics.Timer
import groovy.util.logging.Slf4j
import org.lappsgrid.eager.mining.core.jmx.Registry
import org.lappsgrid.eager.rabbitmq.topic.DataBox

import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream

/**
 * A simple service that accepts a zip file as an incoming byte stream and unzips it into
 * Galaxy's FTP upload directory.  The directory can be specified as a command line argument.
 */
@Slf4j('logger')
class App implements Runnable {
    /** The RabbitMQ message exchange used to send and receive messages. */
    public static final String EXCHANGE = "galaxy.upload.service"

    /** The name of the mailbox the applications uses to receive messages. */
    public static final String MBOX = "zip"

    /** The Galaxy FTP directory where we will unzip files. */
    File galaxy

    /** Object used to synchronize the main thread shutdown. */
    Object semaphore

    // Metrics
    // The total number of bytes (across all requests) processed.
    Histogram bytes
    // The number of requests (messages) received.
    Meter requests
    // Count the errors.
    Counter errors
    // Count the number of files extracted.
    Counter files
    // Track the time to respond to each message.
    Timer timer

    App() {
        this('/home/galaxy/galaxy/database/ftp')
    }

    App(String path) {
        logger.info("Galaxy FTP directory: {}", path)
        semaphore = new Object()
        galaxy = new File(path)
    }

    void run() {
        logger.info("Staring Galaxy upload service")
        // Initialize the JMX metrics
        String localName = 'uploader'
        bytes = Registry.histogram(localName, 'bytes')
        errors = Registry.counter(localName, 'errors')
        requests = Registry.meter(localName, 'requests')
        files = Registry.counter(localName, 'files')
        timer = Registry.timer(localName, 'timer')
        Registry.register(new Manager(this), "org.lappsgrid.eager.mining.upload.Manager:type=Uploader")
        Registry.startJmxReporter()
        logger.debug("Started JMX reporting")

        // Initialize a data box to receive incoming messages.
        DataBox box = new DataBox(EXCHANGE, MBOX) {
            void recv(byte[] data) {
                logger.info("Receiving {} bytes", data.length)
                // Record metrics
                bytes.update(data.length)
                requests.mark()

                ByteArrayInputStream input = new ByteArrayInputStream(data)
                Timer.Context context = timer.time()
                try {
                    byte[] buffer = new byte[1024]
                    ZipInputStream zip = new ZipInputStream(input)
                    ZipEntry entry = zip.getNextEntry()
                    while (entry != null) {
                        // Create the output file and ensure the parent directory exists.
                        File file = new File(galaxy, entry.name)
                        File parent = file.parentFile
                        if (!parent.exists()) {
                            if (!parent.mkdirs()) {
                                throw new IOException("Unable to create directory " + parent.path)
                            }
                        }

                        // Read from the zip and write to the file.
                        FileOutputStream out = new FileOutputStream(file)
                        int len = zip.read(buffer)
                        while (len > 0) {
                            out.write(buffer, 0, len)
                            len = zip.read(buffer)
                        }
                        out.close()
                        files.inc()
                        entry = zip.getNextEntry()
                    }
                }
                catch (Exception e) {
                    logger.error("Unable to unzip incoming payload.", e)
                    errors.inc()
                }
                finally {
                    context.stop()
                    input.close()
                }
            }
        }

        // Sleep until another thread wakes us.
        logger.info("Waiting for uploads.")
        synchronized (semaphore) {
            semaphore.wait()
        }

        // Cleanup
        logger.info("Shutting down.")
        box.close()
        logger.info("Terminated")
    }

    void stop() {
        // Wake up all waiting threads.
        logger.debug("Received stop signal")
        synchronized (semaphore) {
            semaphore.notifyAll()
        }
    }

    static void main(String[] args) {
        App app
        if (args.size() > 0) {
            app = new App(args[0])
        }
        else {
            app = new App()
        }
        app.run()
    }
}
