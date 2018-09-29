package org.lappsgrid.eager.query

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.api.QueryProcessor

/**
 * Returns the input string unchanged.
 */
class IdentityProcessor implements QueryProcessor {

    Query transform(String question) {
        List<String> terms = question.tokenize(' ')
        return new Query(question, question, terms)
    }
}
