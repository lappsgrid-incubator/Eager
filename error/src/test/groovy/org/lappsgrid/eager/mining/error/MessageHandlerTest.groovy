package org.lappsgrid.eager.mining.error

import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.rabbitmq.pubsub.Publisher
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 *
 */
class MessageHandlerTest {

    // The MessageHandler does not listen for broadcasts.
    @Ignore
    void unrecognized_broadcast() {

        Configuration c = new Configuration()
        MessageHandler handler = new MessageHandler()
        handler.start()

        Publisher pub = new Publisher(c.BROADCAST)
        pub.publish('foobar')
        sleep(2000)
        pub.close()
        handler.close()
    }

    // The MessageHandler does not listen for broadcasts.
    @Ignore
    void shutdown_broadcast() {
        Configuration c = new Configuration()
        MessageHandler handler = new MessageHandler()
        handler.start()

        Publisher pub = new Publisher(c.BROADCAST)
        pub.publish('shutdown')
        sleep(2000)
    }

    @Test
    void send_message() {
        Configuration c = new Configuration()
        MessageHandler handler = new MessageHandler()
        handler.start()

        PostOffice po = new PostOffice(c.POSTOFFICE)
        po.send(c.BOX_ERROR, "Error message 1")
        po.send(c.BOX_ERROR, "Error message 2")
        po.send(c.BOX_ERROR, "shutdown")

        sleep(2000)
        po.close()
        handler.close()
        assert 3 == handler.count()
    }
}
