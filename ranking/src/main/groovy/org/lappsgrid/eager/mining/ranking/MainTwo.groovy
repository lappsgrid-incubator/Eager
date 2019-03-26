package org.lappsgrid.eager.mining.ranking

import groovy.util.logging.Slf4j
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.ranking.model.Document
import org.lappsgrid.eager.mining.scoring.ConsecutiveTermEvaluator
import org.lappsgrid.eager.mining.scoring.ScoringAlgorithm
import org.lappsgrid.eager.mining.scoring.WeightedAlgorithm
@Grab('org.lappsgrid.eager.mining:rabbitmq:1.2.0')


/**
 *
 */
@Slf4j("logger")
class MainTwo {

    String section
    List<ScoringAlgorithm> algorithms
    Closure field
    float weight

    MainTwo(String section) {
        this.section = section
        algorithms = []
    }

    void add(ScoringAlgorithm algorithm) {
        algorithms.add(algorithm)
    }

    final String EXCHANGE = 'ranking.postoffice'
    PostOffice po = new PostOffice(EXCHANGE)


    //ConsecutiveTermEvaluator
    MessageBox CTE = new MessageBox(EXCHANGE, 'ConsecutativeTermEvaluator') {
        @Override
        void recv(Message message) {
            ScoringAlgorithm algorithm = new ConsecutiveTermEvaluator()
            // Convert message.body string back to document
            def field = field(message.body)
            float score = 0.0f

            if (field instanceof String) {
                score = algorithm.score(query, field)
            }
            else if (field instanceof Section) {
                score = algorithm.score(query, field)
            }
            else if (field instanceof Collection) {
                field.each { item ->
                    score += algorithm.score(query, field)
                }
            }

            message.body = score
            po.send(message)
        }
    }
    //FirstSentenceEvaluator
    //PercentageOfTermsEvaluator
    //SentenceCountEvaluator
    //TermFrequencyEvaluator
    //TermOrderEvaluator
    //TermPositionEvaluator

    List<Document> rank(Query query, List<Document> documents) {
        logger.info("Ranking {} documents.", documents.size())
        documents.each { Document document ->
            float total = 0.0f
            algorithms.each { algorithm ->
                route = [algorithm.name(), 's']
                def field = field(document)
                //How to send full document instead of text of that document? override message constructor?
                //What to do with query?
                x = new Message('', field, route)
                po.send(x)
            }

            MessageBox sum = new MessageBox(EXCHANGE, 's') {
                @Override
                void recv(Message message) {

                    total += Float.parseFloat(message.body)

                    //Does this work within messagebox?
                    document.addScore(section, algorithm.abbrev(), Float.parseFloat(message.body))

                    latch.countDown()
                }
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
