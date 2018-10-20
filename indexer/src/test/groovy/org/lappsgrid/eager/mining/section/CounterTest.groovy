package org.lappsgrid.eager.mining.section

import org.junit.Test

/**
 *
 */
class CounterTest {

    @Test
    void testNext() {
        Counter counter = new Counter()
        assert 0 == counter.count
        ++counter
        assert 1 == counter.count
        assert 2 == (++counter).count
        counter++
        assert 3 == counter.count
    }
}
