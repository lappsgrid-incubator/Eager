package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 *
 */
interface Evaluator {

    float evaluate(Query query, Document document)
}
