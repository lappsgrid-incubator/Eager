package org.lappsgrid.eager.mining.ranking

import com.codahale.metrics.Meter
import com.codahale.metrics.Timer
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.core.jmx.Registry
import org.lappsgrid.eager.mining.model.Section
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
    CountDownLatch latch = new CountDownLatch(algorithms.size())

    // Make one of these for each algorithm, then sum the scores after they are all sent back to origin
    // Questions:
    // 1) Need to find way to send message to correct location (algorithm) based on algorithms needed
    // 2) Need to keep track of score after receive
    //    Maybe have one worker aggregate the scores then send message to main telling it to process next document?
    // 3) Fix the import issues

    //ConsecutiveTermEvaluator
    MessageBox box1 = new MessageBox(EXCHANGE, '1') {
        @Override
        void recv(Message message) {
            def field = field(message.body)
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
            total += score

            //Send back value to calculate total
            message.body = total
            po.send(message)
        }
    }

    float calculate_update(algorithms, document){
        float weight = 0.0f

        algorithms.each { algorithm ->
            def field = field(document)
            route = algorithm + 's'
            x = new Message('', document, route)
            po.send(x)

        }


        MessageBox sum = new MessageBox(EXCHANGE, 's') {
            @Override
            void recv(Message message) {
                weight += message.body
                latch.countDown()
            }
        }
        return weight

    }





    public static void main(String[] args) {

        }
}
