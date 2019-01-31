package org.lappsgrid.eager.mining.retrieval

import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.mining.core.Configuration
import org.lappsgrid.eager.mining.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

import static org.junit.Assert.*

/**
 *
 */
@Ignore
public class DocumentLoaderTest
{
    static final String BOX = 'test.loader'
    Configuration c
    DocumentLoader loader
    PostOffice office
    MailBox box
    Object semaphore
    boolean passed

    @Before
    public void setup()
    {
        passed = false
        semaphore = new Object()
        c = new Configuration()
        loader = new DocumentLoader()
        loader.start()
        office = new PostOffice(c.POSTOFFICE)
        box = new MailBox(c.POSTOFFICE, BOX) {
            void recv(String message) {
                process(message)
            }
        }
    }

    @After
    public void teardown()
    {
        semaphore = null
        c = null

        loader.stop()
        loader = null

        office.close()
        office = null

        box.close()
        box = null
    }

    @Test
    public void load()
    {
        Message message = new Message()
            .command('load')
            .body('src/test/resources/hello.txt')
            .route('load')
            .route(BOX)
        office.send(message)
        println "Message sent"
        synchronized (semaphore) {
            semaphore.wait(20000)
        }
        assert passed
        println "DocumentLoaderTest.load"
    }

//    @Test
    public void stop()
    {
    }

    void process(String json) {
        Message message = Serializer.parse(json, Message)
        if ('loaded' != message.command) {
            fail message.body ?: 'File not loaded.'
        }
        passed = true
        println "Message received: ${message.body}"
        synchronized (semaphore) {
            semaphore.notifyAll()
        }
    }
}