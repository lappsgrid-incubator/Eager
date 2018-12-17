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
        float actual = evaluator.score(query, 'x y z')
        assert 0.0f == actual
    }

    @Test
    void oneTerm() {
        assert 0 == evaluator.score(query, 'b x x')
        assert 0 == evaluator.score(query, 'x b x a x')
        assert 0 == evaluator.score(query, 'x x b')
    }

    @Test
    void allTerms() {
        float actual = evaluator.score(query, 'a b c')
        assert 1.0f == actual
    }

    @Test
    void twoSpans() {
        closeEnough(4.0f/5.0f, 'a b x a b')
    }

    @Test
    void twoSpansofConsecutiveTerms() {
        closeEnough(5.0/9.0,'a b x x b x a b c')
    }

}
