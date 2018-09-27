package org.lappsgrid.eager.rabbitmq

import org.junit.Test
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 *
 */
class PostOfficeTest {

    @Test
    void sendMail() {
        MailBox box1 = new MailBox('test', 'box1') {
            public void recv(String message) {
                println "Box 1 -> $message"
                true
            }
        }
        MailBox box2 = new MailBox('test', 'box2') {
            public void recv(String message) {
                println "Box 2 -> $message"
                true
            }
        }
        PostOffice post = new PostOffice('test')
        post.send('box1', "box1 message 1")
        post.send('box2', "box2 message 1")
        post.send('box2', "box2 message 2")
        post.send('box1', "box1 message 2")
        sleep(500)
        println "Done"

    }
}
