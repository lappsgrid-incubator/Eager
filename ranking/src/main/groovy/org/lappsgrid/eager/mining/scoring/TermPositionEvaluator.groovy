package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query

/**
 * Terms that appear earlier in the title are scored higher.
 */
class TermPositionEvaluator implements ScoringAlgorithm{

    @Override
    float score(Query query, String input) {
        float length = (float) input.length()
        float total = 0f
        query.terms.each { term ->
            int pos = input.indexOf(term)
            if (pos > 0) {
                total += 1 - (pos / length)
            }
        }
        return total
    }

    @Override
    String name() {
        return 'TermPositionEvaluator'
    }

    @Override
    String abbrev() {
        return "position"
    }
}
