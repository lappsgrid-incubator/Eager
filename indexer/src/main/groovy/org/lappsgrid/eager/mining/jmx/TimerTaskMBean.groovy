package org.lappsgrid.eager.mining.jmx

import javax.management.DynamicMBean

/**
 *
 */
interface TimerTaskMBean {
    void shutdown()

    boolean getRunning()
    void setRunning(boolean running)
}


