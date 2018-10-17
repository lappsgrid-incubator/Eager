package org.lappsgrid.eager.mining.jmx

/**
 *
 */
interface ManagerMBean {
    
    void shutdown()
    
    int getFilesQueueSize()
    int getNodesQueueSize()
    int getSinkQueueSize()
}
