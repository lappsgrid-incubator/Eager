package org.lappsgrid.eager.mining.core

import com.codahale.metrics.Gauge
import org.junit.Test
import org.lappsgrid.eager.mining.core.jmx.Registry

/**
 *
 */
class JmxTest {
    @Test
    void gaugeTest() {
        int x = 0
        Gauge<Integer> gauge = Registry.gauge('total.bytes') {
            return x
        }

        assert x == gauge.value
        x = 100
        assert x == gauge.value
        x = 4
        assert 4 == gauge.value
    }
}
