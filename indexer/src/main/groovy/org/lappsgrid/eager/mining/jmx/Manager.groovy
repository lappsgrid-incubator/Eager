package org.lappsgrid.eager.mining.jmx

/**
 *
 */
class Manager implements ManagerMBean{
    
    Queue<File> fileQueue
    Queue<Node> nodeQueue
    Queue<Set> sectionsQueue
    
    void shutdown() {

    }

    Manager filesQ(Queue<File> files) {
        this.fileQueue = files
        return this
    }
    Manager nodesQ(Queue<Node> nodes) {
        nodeQueue = nodes
        this
    }
    Manager sectionsQ(Queue<Set> sections) {
        sectionsQueue = sections
        this
    }
    
    int getFilesQueueSize() {
        return fileQueue.size()
    }

    int getNodesQueueSize() {
        return nodeQueue.size()
    }

    int getSinkQueueSize() {
        return sectionsQueue.size()
    }
}
