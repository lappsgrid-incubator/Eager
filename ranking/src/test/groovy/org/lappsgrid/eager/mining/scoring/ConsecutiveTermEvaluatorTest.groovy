package org.lappsgrid.eager.mining.scoring

import org.junit.*
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.api.QueryProcessor

//import static org.junit.Assert.*

/**
 *
 */
class ConsecutiveTermEvaluatorTest {

    ScoringAlgorithm evaluator
    Query query

    @Before
    void setup() {
        evaluator = new ConsecutiveTermEvaluator()
        query = makeQuery('a b c')
    }

    @After
    void teardown() {
        evaluator = null
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
        assert closeEnough(4.0f/5.0f, evaluator.score(query, 'a b x a b'))
    }

    @Test
    void twoSpansofConsecutiveTerms() {
        assert (5.0/9.0) == evaluator.score(query, 'a b x x b x a b c')
    }

    Query makeQuery(String question) {
        List<String> tokens = question.trim().toLowerCase().split(/\s+/)
        return new Query().question(question).terms(tokens)
    }

    boolean closeEnough(double expected, double actual) {
        float delta = 0
        if (actual < expected) {
            delta = expected - actual
        }
        else {
            delta = actual - expected
        }
        return delta < 0.00001f
    }
}
