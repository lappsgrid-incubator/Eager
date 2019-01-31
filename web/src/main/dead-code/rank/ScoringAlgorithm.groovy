package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 * Interface for any component that calculates a score for the input document.
 */
interface ScoringAlgorithm {

    float score(Query query, String input)
    String name()
    String abbrev()
}
