package org.lappsgrid.eager.mining.ranking

import groovy.util.logging.Slf4j
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.ranking.model.Document
import org.lappsgrid.eager.mining.scoring.ScoringAlgorithm
import org.lappsgrid.eager.mining.scoring.WeightedAlgorithm
@Grab('org.lappsgrid.eager.mining:rabbitmq:1.2.0')


/**
 *
 */
@Slf4j("logger")
class RankingEngine {

    String section
    List<ScoringAlgorithm> algorithms
    Closure field
    float weight

    RankingEngine(String section) {
        this.section = section
        algorithms = []
    }

    void add(ScoringAlgorithm algorithm) {
        algorithms.add(algorithm)
    }



    //Change this code to only process a single document at a time
    List<Document> rank(Query query, List<Document> documents) {
        logger.info("Ranking {} documents.", documents.size())
        documents.each { Document document ->
            float total = 0.0f
            algorithms.each { algorithm ->
                def field = field(document)

                float score = 0.0f
                if (field instanceof String) {
                    score = calculate(algorithm, query, field)
                }
                else if (field instanceof Section) {
                    score = calculate(algorithm, query, field)
                }
                else if (field instanceof Collection) {
                    field.each { item ->
                        score += calculate(algorithm, query, item)
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



    float calculate(WeightedAlgorithm algorithm, Query query, field) {
        float result = algorithm.score(query, field)
        if (Float.isNaN(result)) {
            return 0f
        }
        return result
    }
}
