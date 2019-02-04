package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.model.Sentence
import org.lappsgrid.eager.mining.model.Token

/**
 * Returns 1 if the first sentence in the section contains any search terms.
 */
class FirstSentenceEvaluator extends AbstractScoringAlgorithm {
    @Override
    float score(Query query, Section section) {
        Set<String> found = new HashSet<>()
        if (section.sentences.size() > 0) {
            Sentence s = section.sentences[0]
            for (Token t : s.tokens) {
                if (contains(query.terms, t)) {
                    found.add(t.lemma)
                }
            }
        }
        return ((float)found.size()) / query.terms.size()
    }

    @Override
    String name() {
        return "FirstSentenceEvaluator"
    }

    @Override
    String abbrev() {
        return "1stSent"
    }
}
