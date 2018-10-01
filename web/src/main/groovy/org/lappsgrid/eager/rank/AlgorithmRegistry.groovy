package org.lappsgrid.eager.rank

/**
 *
 */
class AlgorithmRegistry {

    Map<String,ScoringAlgorithm> algorithms = [
            '1': { new ConsecutiveTermEvaluator() },
            '2': { new HowManyTermsInTitle() },
            '3': { new TermPositionEvaluator() },
            '4': { new WordsInTitleThatAreSearchTerms() }
    ]

    ScoringAlgorithm get(String id) {
        Closure constructor = algorithms[id]
        return constructor()
    }
}
