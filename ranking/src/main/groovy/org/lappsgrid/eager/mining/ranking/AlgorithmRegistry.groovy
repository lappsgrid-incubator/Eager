package org.lappsgrid.eager.mining.ranking

import org.lappsgrid.eager.mining.scoring.ConsecutiveTermEvaluator
import org.lappsgrid.eager.mining.scoring.FirstSentenceEvaluator
import org.lappsgrid.eager.mining.scoring.PercentageOfTermsEvaluator
import org.lappsgrid.eager.mining.scoring.ScoringAlgorithm
import org.lappsgrid.eager.mining.scoring.SentenceCountEvaluator
import org.lappsgrid.eager.mining.scoring.TermFrequencyEvaluator
import org.lappsgrid.eager.mining.scoring.TermOrderEvaluator
import org.lappsgrid.eager.mining.scoring.TermPositionEvaluator

/**
 *
 */
class AlgorithmRegistry {

    static Map<String, ScoringAlgorithm> algorithms = [
            '1': { new ConsecutiveTermEvaluator() },
            '2': { new PercentageOfTermsEvaluator() },
            '3': { new TermPositionEvaluator() },
            '4': { new TermFrequencyEvaluator() },
            '5': { new TermOrderEvaluator() },
            '6': { new FirstSentenceEvaluator() },
            '7': { new SentenceCountEvaluator() }
    ]

    static ScoringAlgorithm get(String id) {
        Closure constructor = algorithms[id]
        if (!constructor) {
            return null
        }
        return constructor()
    }
}
