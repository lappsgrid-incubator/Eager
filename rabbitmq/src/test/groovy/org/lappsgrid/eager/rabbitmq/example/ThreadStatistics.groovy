package org.lappsgrid.eager.rabbitmq.example

import org.lappsgrid.eager.rabbitmq.Message

/**
 * Just to demonstrate that we have achieved parallel process across multiple threads we keep
 * track of the threads each worker is run on,
 */
class ThreadStatistics {

    /** The threads each worker has run on. */
    Map<String, Set<String>> byWorker = [:]
    /** The workers each thread has run. */
    Map<String, Set<String>> byThread = [:]

    void record(Message message) {
        Map data = (Map) message.body
        save(data.id, data.thread, byWorker)
        save(data.thread, data.id, byThread)
    }

    void save(String key, String value, Map<String, Set<String>> map) {
        Set<String> list = map[key]
        if (list == null) {
            list = []
            map[key] = list
        }
        list.add(value)
    }

    void print(PrintStream out) {
        out.println("Workers")
        print(out, byWorker)
        out.println()
        out.println("Threads: ${byThread.size()}")
        print(out, byThread)
    }

    void print(PrintStream out, Map<String, Set<String>> map) {
        map.keySet().sort().each { String key ->
            Set<String> list = map[key]
            out.println "$key\t${list.size()}\t${list.join(", ")}"
        }
    }
}
