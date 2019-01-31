package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.model.Token

/**
 * Terms that appear earlier in the title are scored higher.
 */
class TermPositionEvaluator implements ScoringAlgorithm{

    @Override
    float score(Query query, Section section) {
        float length = (float) section.tokens.size()
        float total = 0f
        query.terms.each { term ->
            int pos = 0
            for (Token t : section.tokens) {
                ++pos
                if (t.word == term || t.lemma == term) {
                    total += 1 - (pos / length)
                    break
                }
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
