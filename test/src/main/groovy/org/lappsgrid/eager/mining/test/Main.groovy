package org.lappsgrid.eager.mining.test

import com.codahale.metrics.Meter
import com.codahale.metrics.Slf4jReporter
import com.codahale.metrics.Timer
import groovy.util.logging.Slf4j
import org.lappsgrid.eager.core.jmx.Registry

import java.util.concurrent.TimeUnit

/**
 *
 */
@Slf4j("logger")
class Main implements MainMBean {

    String salutation
    boolean running
    final Meter meter
    final Timer timer
    Random random

    Main() {
        salutation = "Hello"
        meter = Registry.meter("main.meter")
        timer = Registry.timer("main.timer")
    }

    void run() {
        logger.info("Process is starting.")
        Registry.register(this, "org.lappsgrid.eager.mining.test.main:type=Main,name=TestMain")
        Registry.startJmxReporter()
        Registry.startLogReporter("org.lappgrid.eager.mining.metrics", 10, TimeUnit.SECONDS)
//        Registry.startLogReporter(logger)

        random = new Random()
        running = true
        while (running) {
            meter.mark()
            long delay = random.nextLong() % 10_000
            if (delay < 0) {
                delay = -delay
            }
            logger.info("Sleeping for {} msec", delay)
            Timer.Context context = timer.time()
            sleep(delay)
            context.stop()
        }
        Registry.stopJmxReporter()
        Registry.stopLogReporter()
        logger.info("Process is terminating.")
    }

    void shutdown() {
        logger.info "Shutting down."
        running = false
    }

    String greet(String name) {
        logger.info("Sending greeting for {}", name)
        return "$salutation $name"
    }

    void salutation(String salutation) {
        logger.info("Setting salutation to {}", salutation)
        this.salutation = salutation
    }

    static void main(String[] args) {
        new Main().run()
    }
}

