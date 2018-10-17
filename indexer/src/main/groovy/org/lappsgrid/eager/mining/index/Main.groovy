package org.lappsgrid.eager.mining.index

import com.codahale.metrics.MetricRegistry

import javax.management.MBeanServer
import java.lang.management.ManagementFactory

/**
 *
 */
class Main {

    public static final MetricRegistry registry = new MetricRegistry()

    static final String POSTOFFICE = "eager.index.postoffice"

    void run() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer()
    }

    static String name(String... parts) {
        return "org.lappsgrid.eager.mining." + parts.join(".")
    }

    static void main(String[] args) {

    }
}
