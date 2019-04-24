package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section

/**
 * How many words in the passage are terms in the question.
 */
class TermFrequencyEvaluator implements ScoringAlgorithm {
    @Override
    float score(Query query, Section section) {
        int count = 0
        section.tokens.each { token ->
            if (query.contains(token)) {
                ++count
            }
        }
        return ((float)count) / section.tokens.size()
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
