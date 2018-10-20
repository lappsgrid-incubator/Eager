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
        algorithms << new WeightedAlgorithm(new TermFrequencyEvaluator())
        algorithms << new WeightedAlgorithm(new PercentageOfTermsEvaluator())
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
        return rank(query, documents, { doc -> doc.title })
    }

    List<Document> rank(Query query, List<Document> documents, Closure getField) {
        println "Ranking ${documents.size()} documents."
        documents.each { Document document ->
            float total = 0.0f
            algorithms.each { algorithm ->
                def field = getField(document)
                if (field instanceof String) {
                    //println "Scoring string: $field"
                    float score = algorithm.score(query, field)
                    document.scores[algorithm.algorithm.abbrev()] = score
                    total = total + score
//                    println "$document.doi ${algorithm.algorithm.abbrev()} $score"
                }
                else if (field instanceof Collection) {
                    //println "Field is a collection size: ${field.size()}"
                    float thisAlgScore = 0
                    field.each { item ->
//                        println "Scoring collection field: $item"
                        float score = algorithm.score(query, item)
                        total = total + score
                        thisAlgScore += score
                    }
                    document.scores[algorithm.algorithm.abbrev()] = thisAlgScore
                    //println "$document.doi ${algorithm.algorithm.abbrev()} $thisAlgScore"
                }
            }
            document.score = total
//            printf "%s -> %2.6f\n", document.pmid, total
        }
        return documents.sort { a,b -> b.score <=> a.score }
    }
}
