package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section

/**
 * Interface for any component that calculates a score for the input document.
 */
interface ScoringAlgorithm {

    float score(Query query, Section section)
    String name()
    String abbrev()
}
