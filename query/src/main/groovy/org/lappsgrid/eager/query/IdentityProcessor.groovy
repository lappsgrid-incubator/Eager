package org.lappsgrid.eager.query

import org.lappsgrid.eager.mining.api.QueryProcessor

/**
 * Returns the input string unchanged.
 */
class IdentityProcessor implements QueryProcessor {

    String transform(String question) {
        return question
    }
}
