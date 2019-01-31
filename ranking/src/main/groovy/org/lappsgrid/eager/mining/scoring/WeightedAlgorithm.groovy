package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section

/**
 *
 */
class WeightedAlgorithm implements ScoringAlgorithm {
    float weight
    ScoringAlgorithm algorithm

    private String name

    WeightedAlgorithm(ScoringAlgorithm algorithm, float weight=1.0f) {
        this.weight = weight
        this.algorithm = algorithm
        this.name = "Weighted " + algorithm.name()
    }

    float score(Query query, Section section) {
        return weight * algorithm.score(query, section)
    }

    String name() {
        return name
    }

    String abbrev() {
        return algorithm.abbrev()
    }
}
