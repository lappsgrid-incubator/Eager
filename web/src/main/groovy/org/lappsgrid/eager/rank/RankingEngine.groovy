package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 *
 */
class RankingEngine {

    List<WeightedAlgorithm> algorithms


    RankingEngine() {
        algorithms = []
        algorithms << new WeightedAlgorithm(new ConsecutiveTermEvaluator())
        algorithms << new WeightedAlgorithm(new WordsInTitleThatAreSearchTerms())
        algorithms << new WeightedAlgorithm(new HowManyTermsInTitle())
        algorithms << new WeightedAlgorithm(new TermPositionEvaluator())
    }

    RankingEngine(Map params) {
        algorithms = []
        AlgorithmRegistry registry = new AlgorithmRegistry()
        params.each { String key, String value ->
            if (key.startsWith('alg')) {
                float weight = params.get('weight' + value) as Float
                ScoringAlgorithm algorithm = registry.get(value)
                println "Registering algorithm ${algorithm.name()} x $weight"
                algorithms << new WeightedAlgorithm(algorithm, weight)
            }
        }
    }

    List<Document> rank(Query query, List<Document> documents) {
        documents.each { Document document ->
            float total = 0.0f
            algorithms.each { algorithm ->
                float score = algorithm.score(query, document)
                document.scores[algorithm.algorithm.abbrev()] = score
                total = total + score
            }
            document.score = total
//            printf "%s -> %2.6f\n", document.pmid, total
        }
        return documents.sort { a,b -> b.score <=> a.score }
    }
}
