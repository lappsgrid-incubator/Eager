package org.lappsgrid.eager.core.jmx

import com.codahale.metrics.JmxReporter
import com.codahale.metrics.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.management.MBeanServer
import javax.management.ObjectName
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

/**
 *
 */
class Registry {
    private final static MetricRegistry registry = new MetricRegistry()
    private static JmxReporter jmxReporter
    private static Slf4jReporter logReporter

    private static MBeanServer server

    private Registry() { }

    static Meter meter(String... name) {
        return registry.meter(Registry.name(name))
    }

    static Timer timer(String... name) {
        return registry.timer(Registry.name(name))
    }

    static Counter counter(String... name) {
        return registry.counter(Registry.name(name))
    }

    static void startJmxReporter() {
        jmxReporter = JmxReporter.forRegistry(registry).build()
        jmxReporter.start()
    }

    static void stopJmxReporter() {
        jmxReporter.stop()
        jmxReporter = null
    }

    static void startLogReporter(String name) {
        startLogReporter(LoggerFactory.getLogger(name))
    }

    static void startLogReporter(String name, int period, TimeUnit unit) {
        startLogReporter(LoggerFactory.getLogger(name), period, unit)
    }

    static void startLogReporter(Logger logger) {
        startLogReporter(logger, 1, TimeUnit.MINUTES)
    }

    static void startLogReporter(Logger logger, int period, TimeUnit unit) {
        logReporter = Slf4jReporter.forRegistry(registry)
                .outputTo(logger)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build()
        logReporter.start(period, unit)
    }

    static void stopLogReporter() {
        logReporter.stop()
        logReporter = null
    }

    static void register(Object bean, String name) {
        register(bean, new ObjectName(name))
    }

    static void register(Object bean, ObjectName name) {
        if (server == null) {
            createServer()
        }
        server.registerMBean(bean, name)
    }

    private synchronized static createServer() {
        if (server != null) {
            return
        }
        server = ManagementFactory.getPlatformMBeanServer()
    }

    private static String name(String... name) {
        return "org.lappsgrid.eager.mining." + name.join(".")
    }


}
