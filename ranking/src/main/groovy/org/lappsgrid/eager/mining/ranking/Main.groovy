package org.lappsgrid.eager.mining.ranking
import org.lappsgrid.eager.mining.api.Query
import com.codahale.metrics.Meter
import com.codahale.metrics.Timer
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.core.jmx.Registry
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.scoring.ConsecutiveTermEvaluator
import org.lappsgrid.eager.mining.scoring.ScoringAlgorithm
import org.lappsgrid.eager.mining.scoring.WeightedAlgorithm
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import java.util.concurrent.CountDownLatch


class Main {

    final String EXCHANGE = 'ranking.postoffice'
    PostOffice po = new PostOffice(EXCHANGE)

    /** Sure what section is **/
    String section
    /** The list of algorithms used to send to the respective mailboxes **/
    List<ScoringAlgorithm> algorithms
    /** Field of document, either string, section, or collection **/
    Closure field




    Main(String section) {
        this.section = section
        algorithms = []

    }
    void add(ScoringAlgorithm algorithm) {
        algorithms.add(algorithm)
    }
    float calculate(WeightedAlgorithm algorithm, Query query, field) {
        float result = algorithm.score(query, field)
        if (Float.isNaN(result)) {
            return 0f
        }
        return result
    }

    // Need to define routes
    // How are algorithms represented?

    CountDownLatch latch = new CountDownLatch(algorithms.size())


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


    // Should this be a method?
    float calculate_update(algorithms, document){
        float weight = 0.0f

        //How to send full document instead of text of that document? override message constructor?
        algorithms.each { algorithm ->
            route = [algorithm.name(), 's']
            x = new Message('', document, route)
            po.send(x)

        }

        //After
        MessageBox sum = new MessageBox(EXCHANGE, 's') {
            @Override
            void recv(Message message) {

                weight += Float.parseFloat(message.body)

                latch.countDown()
            }
        }
        //Make sure countdown latch is done (all document scores have been sent back) then return weight
        return weight

    }





    public static void main(String[] args) {

        }
}
