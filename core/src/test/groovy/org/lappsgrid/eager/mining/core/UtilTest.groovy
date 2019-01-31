package org.lappsgrid.eager.mining.core

import org.apache.groovy.json.internal.Chr
import org.junit.Test

import java.time.Duration
import static java.time.temporal.ChronoUnit.*
import java.time.temporal.TemporalUnit

/**
 *
 */
class UtilTest {
    @Test
    void oneMsec() {
        assert '0:00:00.001s'  == Utils.format(1)
    }

    @Test
    void oneSecond() {
        assert '0:00:01.000s' == Utils.format(1000)
    }

    @Test
    void oneMinute() {
        assert '0:01:00.000' == Utils.format(60000)
    }

    @Test
    void oneHour() {
        assert '1:00:00.000' == Utils.format(duration(1, HOURS))
    }

    @Test
    void oneDay() {
        assert '1d 00:00:00.000' == Utils.format(duration(1, DAYS))
    }

    @Test
    void longTime() {
        long period = duration(6, DAYS) +
                duration(12, HOURS) +
                duration(15, MINUTES) +
                duration(39, SECONDS) +
                duration(345, MILLIS)

        assert '6d 12:15:39.345' == Utils.format(period)
    }

    long duration(long amount, TemporalUnit unit) {
        return Duration.of(amount, unit).toMillis()
    }
}
