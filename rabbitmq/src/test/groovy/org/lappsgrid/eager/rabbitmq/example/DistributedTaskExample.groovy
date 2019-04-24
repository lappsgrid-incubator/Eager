package org.lappsgrid.eager.rabbitmq.example

import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.RabbitMQ
import org.lappsgrid.eager.rabbitmq.example.factory.ReporterFactory
import org.lappsgrid.eager.rabbitmq.example.factory.SorterFactory
import org.lappsgrid.eager.rabbitmq.example.factory.TokenizerFactory
import org.lappsgrid.eager.rabbitmq.topic.MessageBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *  This example assumes the RabbitMQ server is running on localhost with the username/password "lappsgrid"
 *
 *  $> docker run -d --hostname localhost --name rabbit -p 5672:5672 -p 15672:15672 -e RABBITMQ_DEFAULT_USER=lappsgrid -e RABBITMQ_DEFAULT_PASS=lappgrid rabbitmq:3-management
 *
 *  If using the rabbit script (found in the root of this project) the above can be done with:
 *
 *  $> rabbit start lappsgrid
 */
class DistributedTaskExample {
    static final String HOST = "localhost"
    static final String EXCHANGE = "org.lappsgrid.rabbitmq.example"

    static final String TOKENIZER_Q = "token.q"
    static final String SORTER_Q = "sort.q"
    static final String REPORTER_Q = "reporter.q"

    static final String TOKENIZERS_MBOX = "token.mailbox"
    static final String SORTERS_MBOX = "sort.mailbox"
    static final String REPORTERS_MBOX = "report.mailbox"

    static final String[] DATA = [
            "d e f c b a",
            "the lazy dog jumped over some fox",
            "goodbye cruel world i am leaving you today",
            "fee fie fo fum i smell the blood of an englishman",
            'f e d c b a'
    ] as String[]

    void run() {
        // The number of iterations to run.
        int N = 10

        ThreadStatistics stats = new ThreadStatistics()

        // Latch used to track the work completed.
        CountDownLatch latch = new CountDownLatch(N * DATA.length)

        // Where the workers will exchange messages.
        PostOffice po = new PostOffice(EXCHANGE, HOST)

        // The three stages of the processing pipeline.  Each stage of the pipeline is managed by a QueueManager instance.
        QueueManager tokenMaster = new QueueManager(EXCHANGE, TOKENIZERS_MBOX, HOST, TOKENIZER_Q, new TokenizerFactory(), 3)
        QueueManager sortMaster = new QueueManager(EXCHANGE, SORTERS_MBOX, HOST, SORTER_Q, new SorterFactory(), 4)
        QueueManager reportMaster = new QueueManager(EXCHANGE, REPORTERS_MBOX, HOST, REPORTER_Q, new ReporterFactory(latch), 2)

        // Set up a mailbox to record the thread usage statistic sent by workers.
        MessageBox statsBox = new MessageBox(EXCHANGE, "stats.mbox", HOST) {
            @Override
            void recv(Message message) {
                stats.record(message)
            }
        }

        // Send all the data for processing.
        int id = 0
        N.times {
            DATA.each { String s ->
                ++id
                Message message = new Message()
                    .body(s)
                    .route(TOKENIZERS_MBOX, SORTERS_MBOX, REPORTERS_MBOX)
                    .set("id", "msg$id")
                po.send(message)
            }
        }

        // Wait for all the data to reach the reporters
        println "Waiting for the latch"
        if (!latch.await(60, TimeUnit.SECONDS)) {
            println "ERROR timeout waiting for latch"
        }

        // Since the workers decrement the latch before the super class can ACK the message we need to give
        // the threads a little grace period to ACK all their messages.
        println "Thread cool down period"
        sleep(1000)
        println "Shutting down."
        // Close everything we have created in an orderly fashion.
        po.close()
        tokenMaster.close()
        sortMaster.close()
        reportMaster.close()
        statsBox.close()
        stats.print(System.out)
        println "Done"
    }

    static void main(String[] args) {
        System.setProperty(RabbitMQ.USERNAME_PROPERTY, "lappsgrid")
        System.setProperty(RabbitMQ.PASSWORD_PROPERTY, "lappsgrid")
        new DistributedTaskExample().run()
    }
}
