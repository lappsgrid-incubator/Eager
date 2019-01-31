package org.lappsgrid.eager.mining.scoring

import org.junit.*
import org.lappsgrid.eager.mining.api.Query

//import static org.junit.Assert.*

/**
 *
 */
class ConsecutiveTermEvaluatorTest extends TestBase {

    ScoringAlgorithm create() {
        return new ConsecutiveTermEvaluator()
    }

    @Test
    void noTerms() {
        float actual = evaluator.score(query, makeSection('x y z'))
        assert 0.0f == actual
    }

    @Test
    void oneTerm() {
        assert 0 == evaluator.score(query, makeSection('b x x'))
        assert 0 == evaluator.score(query, makeSection('x b x a x'))
        assert 0 == evaluator.score(query, makeSection('x x b'))
    }

    @Test
    void allTerms() {
        float actual = evaluator.score(query, makeSection('a b c'))
        assert 1.0f == actual
    }

    @Test
    void twoSpans() {
        closeEnough(4.0f/5.0f, makeSection('a b x a b'))
    }

    @Test
    void twoSpansofConsecutiveTerms() {
        closeEnough(5.0/9.0,makeSection('a b x x b x a b c'))
    }

}
