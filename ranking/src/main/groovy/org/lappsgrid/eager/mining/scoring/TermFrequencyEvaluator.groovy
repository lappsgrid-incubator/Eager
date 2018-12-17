package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query

/**
 * How many words in the passage are terms in the question.
 */
class TermFrequencyEvaluator implements ScoringAlgorithm, Tokenizer {
    @Override
    float score(Query query, String input) {
        int count = 0
        String[] tokens = tokenize(input)
        tokens.each { token ->
            if (query.terms.contains(token)) {
                ++count
            }
        }
        return ((float)count) / tokens.length
    }

    @Override
    String name() {
        return 'TermFrequency'
    }

    @Override
    String abbrev() {
        return "freq"
    }
}
