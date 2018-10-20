package org.lappsgrid.eager.mining.jmx

import com.codahale.metrics.Gauge

/**
 * Measure the number of elements in a collection.
 * <p>
 * <strong>Warning</strong>The SizeGauge makes use of the {@code size()} method on the
 * underlying Collection and it is important to make sure the collection has a O(1)
 * implementation of {@code size()} (i.e. maintains a counter of some sort) and not
 * a O(N) implementation as some Java collection classes do.  Both the {@link java.util.ArrayList}
 * and {@link java.util.concurrent.ArrayBlockingQueue} classes provide O(1) {@code size()}
 * methods.
 */
class SizeGauge implements Gauge<Integer> {

    Queue q

    SizeGauge(Queue queue) {
        this.q = queue
    }

    Integer getValue() {
        return q.size()
    }
}
