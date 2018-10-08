package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 * How many words in the title are terms in the question.
 */
class TermFrequencyEvaluator implements ScoringAlgorithm, Tokenizer {
    @Override
    float score(Query query, String input) {
        int count = 0
        tokenize(input).each { token ->
            if (query.terms.contains(token)) {
                ++count
            }
        }
        return ((float)count) / input.length()
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
