package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query

/**
 * Count the number of times consecutive terms appear in the title.
 */
class ConsecutiveTermEvaluator implements ScoringAlgorithm, Tokenizer {
    @Override
    float score(Query query, String input) {
        boolean seen = false
        int count = 0
        String[] tokens = tokenize(input)
        tokens.each { word ->
            if (query.terms.contains(word)) {
                if (seen) {
                    ++count
                }
                seen = true
            }
            else {
                seen = false
            }
        }
        return ((float) count) / tokens.length
    }

    @Override
    String name() {
        return 'ConsecutiveTermEvaluator'
    }
    @Override
    String abbrev() {
        return "ngrams"
    }
}
