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
        int c1 = 0
        int c2 = 0
        MailBox box1 = new MailBox('test.postoffice', 'box1') {
            public void recv(String message) {
                println "Box 1 -> $message"
                ++c1
                true
            }
        }

        MailBox box2 = new MailBox('test.postoffice', 'box2') {
            public void recv(String message) {
                println "Box 2 -> $message"
                ++c2
                true
            }
        }

        PostOffice post = new PostOffice('test.postoffice')
        post.send('box1', "box1 message 1")
        post.send('box2', "box2 message 1")
        post.send('box2', "box2 message 2")
        post.send('box1', "box1 message 2")
        post.send('box2', 'box2 message 3')
        sleep(500)
        post.close()
        box1.close()
        box2.close()
        assert 2 == c1 //box1.count
        assert 3 == c2 //box2.count
        println "Done"

    }
}

/*
class TestBox extends MailBox {
    String id
    int count

    TestBox(String id) {
        super('test.postoffice', id)
        this.id = id
    }

    void recv(String message) {
        ++count
        println "$id -> $message"
    }
}
*/