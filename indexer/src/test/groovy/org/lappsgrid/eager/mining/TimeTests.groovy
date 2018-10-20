package org.lappsgrid.eager.mining

import com.codahale.metrics.*
import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.mining.jmx.TimerTask

import javax.management.MBeanServer
import javax.management.ObjectName
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit


/**
 *
 */
@Ignore
class TimeTests {

    @Test
    void times() {
        long msec = 65535
        println format(msec)
    }

    @Test
    void metrics() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer()
//        TimerTask bean = new TimerTask()
        TimerTask bean = new TimerTask()
        ObjectName name = new ObjectName("org.lappsgrid.eager.mining:type=TimerTest")
        server.registerMBean(bean, name)
        bean.run()
        println "TimeTests.metrics"
    }

    @Test
    void random() {
        10.times {
            println ((int)(Math.random() * 1000))
        }
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
}

