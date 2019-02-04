package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.model.Token

/**
 *
 */
class TermOrderEvaluator implements ScoringAlgorithm {
    @Override
    float score(Query query, Section section) {
        int score = 0
        int terms = 0
        int current = 0
        section.tokens.each { Token token ->
            int index = indexOf(query.terms, token)
            if (index >= 0) {
                ++terms
                if (index > current) {
                    ++score
                }
                current = index
            }
        }
        return ((float) score) / ((float)terms)
    }

    @Override
    String name() {
        return "TermOrderEvaluator"
    }

    @Override
    String abbrev() {
        return "order"
    }

    private int indexOf(List<String> terms, Token token) {
        int index = terms.indexOf(token.word)
        if (index >= 0) {
            return index
        }
        return terms.indexOf(token.lemma)
    }
}
