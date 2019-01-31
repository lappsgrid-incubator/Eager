package org.lappsgrid.eager.mining.scoring

import org.junit.After
import org.junit.Before
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.model.Sentence
import org.lappsgrid.eager.mining.model.Token

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

    Section makeSection(String input) {
        Section section = new Section()
        section.text = input
        Sentence sentence = new Sentence()
        sentence.text = input
        List<String> words = input.tokenize(" ")
        words.each { String word ->
            Token token = new Token()
            token.word = word
            token.lemma = word
            token.pos = 'NN'
            token.category = 'category'
            sentence.tokens.add(token)
            section.tokens.add(token)
        }
        section.sentences.add(sentence)
        return section
    }

    void closeEnough(double expected, String question) {
        assert closeEnough(expected, evaluator.score(query, makeSection(question)))
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
