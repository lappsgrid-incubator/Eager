package org.lappsgrid.eager.mining.web.nlp.stanford

/**
 *
 */
interface StressTestMBean {
    void stop()
    String stats()
    void setMaxOutstanding(int n)
}
