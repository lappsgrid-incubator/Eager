package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.model.Sentence
import org.lappsgrid.eager.mining.model.Token

/**
 *
 */
class SentenceCountEvaluator extends AbstractScoringAlgorithm {
    @Override
    float score(Query query, Section section) {
        int count = 0
        for (Sentence s : section.sentences) {
            for (Token t : s.tokens) {
                if (contains(query.terms, t)) {
                    ++count
                    break
                }
            }
        }
        return ((float) count) / section.sentences.size()
    }

    @Override
    String name() {
        return "SentenceCountEvaluator"
    }

    @Override
    String abbrev() {
        return "sents"
    }
}
