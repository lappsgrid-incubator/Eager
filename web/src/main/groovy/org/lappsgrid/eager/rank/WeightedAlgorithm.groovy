package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

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

    float score(Query query, Document document) {
        return weight * algorithm.score(query, document)
    }

    String name() {
        return name
    }

    String abbrev() {
        return algorithm.abbrev()
    }
}
