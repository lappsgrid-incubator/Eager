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
    // How are algorithms represented?
    MessageBox box1 = new MessageBox(EXCHANGE, '1') {
        @Override
        void recv(Message message) {
            // Convert message.body string back to document
            def field = field(message.body)
            float score = 0.0f

            //Change all algorithms into consecutive term one (back to representation)
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

            message.body = total
            po.send(message)
        }
    }

    // Should this be a method?
    float calculate_update(algorithms, document){
        float weight = 0.0f

        //Again how are algorithms represented
        //How to send full document instead of text of that document? override message constructor?
        algorithms.each { algorithm ->
            route = algorithm + 's'
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
