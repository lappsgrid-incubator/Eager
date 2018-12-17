package org.lappsgrid.eager.mining.scoring

import org.junit.After
import org.junit.Before
import org.lappsgrid.eager.mining.api.Query

/**
 *
 */
abstract class TestBase {

    ScoringAlgorithm evaluator
    Query query

    @Before
    void setup() {
        evaluator = create()
        query = makeQuery('a b c')
    }

    @After
    void teardown() {
        evaluator = null
        query = null;
    }

    abstract ScoringAlgorithm create();

    Query makeQuery(String question) {
        List<String> tokens = question.trim().toLowerCase().split(/\s+/)
        return new Query().question(question).terms(tokens)
    }

    void closeEnough(double expected, String question) {
        assert closeEnough(expected, evaluator.score(query, question))
    }

    boolean closeEnough(double expected, double actual) {
        float delta = 0
        if (actual < expected) {
            delta = expected - actual
        }
        else {
            delta = actual - expected
        }
        return (delta < 0.00001f)
    }
}
