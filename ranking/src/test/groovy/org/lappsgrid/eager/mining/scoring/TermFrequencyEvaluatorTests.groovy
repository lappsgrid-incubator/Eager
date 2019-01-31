package org.lappsgrid.eager.mining.scoring

import org.junit.Test

/**
 *
 */
class TermFrequencyEvaluatorTests extends TestBase {

    ScoringAlgorithm create() {
        return new TermFrequencyEvaluator()
    }

    @Test
    void allTerms() {
        closeEnough(1.0, 'a b c')
    }

    @Test
    void oneThird() {
        closeEnough(1.0/3.0, 'a x x')
    }

    @Test
    void oneQuarter() {
        closeEnough(1.0/4.0, 'x x b x')
    }

    @Test
    void twoOfFive() {
        closeEnough(2.0/5.0, 'x a b x x')
    }

    @Test
    void allTheSameTerm() {
        closeEnough(1.0, 'a a a')
        closeEnough(1.0, 'b b b')
        closeEnough(1.0, 'c c c')
    }
}
