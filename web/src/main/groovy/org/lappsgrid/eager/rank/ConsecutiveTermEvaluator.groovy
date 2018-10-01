package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 * Count the number of times consecutive terms appear in the title.
 */
class ConsecutiveTermEvaluator implements ScoringAlgorithm, Tokenizer {
    @Override
    float score(Query query, Document document) {
        boolean seen = false
        int count = 0
        String[] tokens = tokenize(document.title)
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
}
