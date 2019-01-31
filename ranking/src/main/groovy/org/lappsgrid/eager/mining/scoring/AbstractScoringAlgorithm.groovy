package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.model.Token

/**
 *
 */
abstract class AbstractScoringAlgorithm implements ScoringAlgorithm {
    boolean contains(List<String> strings, Token token) {
        return strings.contains(token.word) || strings.contains(token.lemma)
    }
}
