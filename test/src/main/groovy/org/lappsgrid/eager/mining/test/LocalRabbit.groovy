package org.lappsgrid.eager.mining.test

import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MessageBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 * Experimenting with RabbitMQ running on localhost
 */
class LocalRabbit {

    void run() {
        Object semaphore = new Object()
        MessageBox box = new MessageBox("exchange", "inbox", "localhost") {
            void recv(Message message) {
                println "Received " + message.body
                synchronized (semaphore) {
                    semaphore.notifyAll()
                }
            }
        }

        println "Sending a message"
        PostOffice post = new PostOffice("exchange", "localhost")
        Message message = new Message().body("Hello world").route("inbox")
        post.send(message)

        synchronized (semaphore) {
            semaphore.wait()
        }

        println "Closing the connection"
        post.close()
        box.close()
        println "Done"
    }

    static void main(String[] args) {
        new LocalRabbit().run()
    }
}
