package org.lappsgrid.eager.mining.scoring

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.lappsgrid.eager.mining.api.Query

/**
 *
 */
class PercentageOfTermsEvaluatorTest extends TestBase {

    ScoringAlgorithm create() {
        return new PercentageOfTermsEvaluator()
    }

    @Test
    void noneMatch() {
        closeEnough(0f, 'x y z')
    }

    @Test
    void allMatch() {
        closeEnough(1.0f, 'a b c')
    }

    @Test
    void oneThird() {
        closeEnough(1.0/3.0, 'a x x')
    }

    @Test
    void twoThirds1() {
        closeEnough(2.0/3.0, 'a b x')
    }

    @Test
    void twoThirds2() {
        closeEnough(2.0/3.0, 'a x c')
    }

    @Test
    void twoThirds3() {
        closeEnough(2.0/3.0, 'x b c')
    }

    @Test
    void oneThird2() {
        closeEnough(1.0/3.0, 'a a x')

    }

    @Test
    void oneThird3() {
        closeEnough(1.0/3.0, 'a a a')

    }

    @Test
    void twoThird3() {
        closeEnough(2.0/3.0, 'a b a')

    }

}
