package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query

/**
 * Interface for any component that calculates a score for the input document.
 */
interface ScoringAlgorithm {

    float score(Query query, String input)
    String name()
    String abbrev()
}
