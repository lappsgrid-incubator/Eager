package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query

/**
 *  How many terms appear in the passage.
 */
class PercentageOfTermsEvaluator implements ScoringAlgorithm {
    @Override
    float score(Query query, String input) {
        int count = 0
        query.terms.each { term ->
            if (input.contains(term)) {
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
        return "% terms"
    }
}
