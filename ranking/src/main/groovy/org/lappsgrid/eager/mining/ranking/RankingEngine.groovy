package org.lappsgrid.eager.mining.ranking

import groovy.util.logging.Slf4j
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.ranking.model.Document
import org.lappsgrid.eager.mining.scoring.ScoringAlgorithm
import org.lappsgrid.eager.mining.scoring.WeightedAlgorithm

/**
 *
 */
@Slf4j("logger")
class RankingEngine {

    String section
    List<ScoringAlgorithm> algorithms
    Closure field
    float weight

//    RankingEngine() {
//        this("default")
//    }

    RankingEngine(String section) {
        this.section = section
        algorithms = []
//        algorithms << new WeightedAlgorithm(new ConsecutiveTermEvaluator())
//        algorithms << new WeightedAlgorithm(new TermFrequencyEvaluator())
//        algorithms << new WeightedAlgorithm(new PercentageOfTermsEvaluator())
//        algorithms << new WeightedAlgorithm(new TermPositionEvaluator())
    }

//    RankingEngine(Map params) {
//        this("default", params)
//    }

//    RankingEngine(String name, Map params) {
//        this.name = name
//        algorithms = []
//        AlgorithmRegistry registry = new AlgorithmRegistry()
//        params.each { String key, String value ->
//            if (key.startsWith('alg')) {
//                float weight = params.get('weight' + value) as Float
//                ScoringAlgorithm algorithm = registry.get(value)
//                println "Registering $name algorithm ${algorithm.name()} x $weight"
//                algorithms << new WeightedAlgorithm(algorithm, weight)
//            }
//        }
//    }

    void add(ScoringAlgorithm algorithm) {
        algorithms.add(algorithm)
    }

    Document scoreDocument(Query query, Document document){
        float total = 0.0f
        algorithms.each { algorithm ->
            def field = field(document)

            float score = 0.0f
            if (field instanceof String) {
//                    score = algorithm.score(query, field)
                score = calculate(algorithm, query, field)
            }
            else if (field instanceof Section) {
                score = calculate(algorithm, query, field)
            }
            else if (field instanceof Collection) {
                field.each { item ->
//                        score += algorithm.score(query, item)
                    score += calculate(algorithm, query, item)
                }
            }
            logger.trace("{} -> {}", algorithm.abbrev(), score)
            total += score
            document.addScore(section, algorithm.abbrev(), score)
        }
        document.score += total * weight
        logger.trace("Document {} {}", document.id, document.score)
        //return total * weight
        return document
    }





    List<Document> rank(Query query, List<Document> documents) {
        logger.info("Ranking {} documents.", documents.size())
        documents.each { Document document ->
            float total = 0.0f
            algorithms.each { algorithm ->
                logger.info("Calculating Score for {} .", algorithm.name())
                def field = field(document)
                float score = 0.0f
                if (field instanceof String) {
                    score = calculate(algorithm, query, field)
                    logger.info("Field is string")
                }
                else if (field instanceof Section) {
                    score = calculate(algorithm, query, field)
                    logger.info("Field is section")

                }
                else if (field instanceof Collection) {
                    field.each { item ->
//                        score += algorithm.score(query, item)
                        score += calculate(algorithm, query, item)
                        logger.info("Field is collection")

                    }
                }
                logger.trace("{} -> {}", algorithm.abbrev(), score)
                total += score
                document.addScore(section, algorithm.abbrev(), score)
            }
            document.score += total * weight
            logger.trace("Document {} {}", document.id, document.score)
        }
    }

    float calculate(WeightedAlgorithm algorithm, Query query, Section field) {
        logger.info("Calc score for {}.", algorithm.name())
        float result = algorithm.score(query, field)
        if (Float.isNaN(result)) {
            logger.debug("NaN weight for {}.", algorithm.name())
            return 0f
        }
        return result
    }
}
