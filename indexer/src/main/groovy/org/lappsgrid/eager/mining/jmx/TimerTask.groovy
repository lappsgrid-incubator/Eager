package org.lappsgrid.eager.mining.jmx

import com.codahale.metrics.ConsoleReporter
import com.codahale.metrics.JmxReporter
import com.codahale.metrics.MetricRegistry
import com.codahale.metrics.Timer

import java.util.concurrent.TimeUnit

/**
 *
 */
class TimerTask implements TimerTaskMBean {

    boolean running = false

    void shutdown() {
        running = false
    }

    boolean getRunning() {
        return running
    }

    void setRunning(boolean running) {
        this.running = running
    }

    void run() {
        running = true
        MetricRegistry registry = new MetricRegistry()
        Timer timer = registry.timer("test.timer")
        ConsoleReporter reporter = ConsoleReporter.forRegistry(registry)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .convertRatesTo(TimeUnit.SECONDS)
                .build()
        reporter.start(5, TimeUnit.SECONDS)
        JmxReporter jmxReporter = JmxReporter.forRegistry(registry)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .convertRatesTo(TimeUnit.SECONDS)
                .build()
        jmxReporter.start()
        while (running) {
            Timer.Context context = timer.time()
            int delay = Math.random() * 1000
            sleep(delay)
            context.stop()
        }
        sleep(5000)
        reporter.stop()
        println "${this.class.name} halted"
    }
}