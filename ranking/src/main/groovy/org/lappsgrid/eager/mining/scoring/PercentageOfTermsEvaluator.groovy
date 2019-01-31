package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.model.Token

/**
 *  How many terms appear in the passage.
 */
class PercentageOfTermsEvaluator extends AbstractScoringAlgorithm {
    @Override
    float score(Query query, Section section) {
        int count = 0
        query.terms.each { term ->
            Token token = section.tokens.find { it.word == term }
            if (token) {
                ++count
            }
        }
        return ((float) count) / query.terms.size()
    }

    @Override
    String name() {
        return 'PercentageOfTerms'
    }

    @Override
    String abbrev() {
        return "pterms"
    }
}
