package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 * Terms that appear earlier in the title are scored higher.
 */
class TermPositionEvaluator implements ScoringAlgorithm{

    @Override
    float score(Query query, Document document) {
        float length = (float) document.title.length()
        float total = 0f
        query.terms.each { term ->
            int pos = document.title.indexOf(term)
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
