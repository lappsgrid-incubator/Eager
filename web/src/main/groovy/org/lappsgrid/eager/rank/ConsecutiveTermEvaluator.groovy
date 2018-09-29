package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 * Count consecutive terms in the title.
 */
class ConsecutiveTermEvaluator implements Evaluator {
    @Override
    float evaluate(Query query, Document document) {
        boolean seen = false
        int count = 0
        document.title
    }
}
