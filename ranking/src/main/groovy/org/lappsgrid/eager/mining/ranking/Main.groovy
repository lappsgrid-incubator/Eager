package org.lappsgrid.eager.mining.ranking

import com.codahale.metrics.Meter
import com.codahale.metrics.Timer
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.core.jmx.Registry
import org.lappsgrid.eager.mining.scoring.ScoringAlgorithm
import org.lappsgrid.eager.mining.scoring.WeightedAlgorithm
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice


class Main {
    /** The number of documents processed. */
    final Meter count = Registry.meter('nlp', 'count')
    /** The number of errors encountered. */
    final Meter errors = Registry.meter('nlp', 'errors')
    /** Processing time for documents. */
    final Timer timer = Registry.timer('nlp', 'timer')
    /** Object used to block/wait until the queue is closed. */
    Object semaphore
    /** Where outgoing messages are sent. */
    PostOffice post
    /** Where we receive incoming messages. */
    MailBox box

    /** Sure what section is **/
    String section
    /** The list of algorithms used to send to the respective mailboxes **/
    List<ScoringAlgorithm> algorithms
    /** Field of document, either string, section, or collection **/
    Closure field
    /** The weight used to rank each document **/
    float weight


    //NEXT STEP - CREATE WORKERS FOR EACH ALGORITHM AND LOOP MESSAGES BACK TO CALCULATE SCORE


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


    public static void main(String[] args) {

        }
}
