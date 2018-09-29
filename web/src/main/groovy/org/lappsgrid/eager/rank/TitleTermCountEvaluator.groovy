package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 *  How many terms appear in the title
 */
class TitleTermCountEvaluator implements Evaluator {
    @Override
    float evaluate(Query query, Document document) {
        int count = 0
        String title = document.title
        query.terms.each { term ->
            if (title.contains(term)) {
                ++count
            }
        }
        return ((float) count) / query.terms.size()
    }
}
