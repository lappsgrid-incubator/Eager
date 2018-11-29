package org.lappsgrid.eager.mining.error

/**
 *
 */
interface MessageHandlerMBean {
    void close()
    String collect()
    String tail()
    String tail(int n)
    String head()
    String head(int n)
    long count()
    String version()
}
