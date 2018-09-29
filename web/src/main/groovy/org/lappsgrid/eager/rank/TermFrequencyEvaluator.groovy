package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 * How many words in the title are terms from the question.
 */
class TermFrequencyEvaluator implements Evaluator {
    @Override
    float evaluate(Query query, Document document) {
        int count = 0
        document.title.trim().split("\\W+").each { token ->
            if (query.terms.contains(token)) {
                ++count
            }
            return ((float)count) / document.title.length()
        }
    }
}
