package org.lappsgrid.eager.mining.section

/**
 *
 */
class Counter {
    long count

    Counter next() {
        ++count
        return this
    }

    String toString() {
        return Long.toString(count)
    }
}
