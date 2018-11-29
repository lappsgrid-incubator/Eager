package org.lappsgrid.eager.mining.jmx

import org.lappsgrid.eager.mining.io.DirectoryLister
import org.lappsgrid.eager.mining.io.Lister

/**
 *
 */
class Manager implements ManagerMBean{
    
//    Queue<File> fileQueue
//    Queue<Node> nodeQueue
//    Queue<Set> sectionsQueue

    Lister lister

    Manager(Lister lister) {
        this.lister = lister
    }

    void shutdown() {
        lister.terminate()
    }

//    Manager filesQ(Queue<File> files) {
//        this.fileQueue = files
//        return this
//    }
//    Manager nodesQ(Queue<Node> nodes) {
//        nodeQueue = nodes
//        this
//    }
//    Manager sectionsQ(Queue<Set> sections) {
//        sectionsQueue = sections
//        this
//    }
//
//    int getFilesQueueSize() {
//        return fileQueue.size()
//    }
//
//    int getNodesQueueSize() {
//        return nodeQueue.size()
//    }
//
//    int getSinkQueueSize() {
//        return sectionsQueue.size()
//    }
}
