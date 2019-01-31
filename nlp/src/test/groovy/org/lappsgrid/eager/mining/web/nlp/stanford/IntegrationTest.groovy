package org.lappsgrid.eager.mining.web.nlp.stanford

import org.junit.*
import org.lappsgrid.eager.mining.core.json.Serializer
import org.lappsgrid.serialization.DataContainer

import static org.lappsgrid.discriminator.Discriminators.*

import org.lappsgrid.eager.mining.core.Configuration
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.lif.Container

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 *
 */
@Ignore
class IntegrationTest {
    public static final String TEXT = "Karen flew to New York. Nancy flew to Bloomington."
    static final Configuration c = new Configuration()
    static final String SELF = 'stanford-test-return'

    PostOffice po

    @Before
    void setup() {
        po = new PostOffice(c.POSTOFFICE)
    }

    @After
    void teardown() {
        po.close()
        po = null
    }

    @Test
    void test() {
        String json
        CountDownLatch latch = new CountDownLatch(1)
        MailBox box = new MailBox(c.POSTOFFICE, SELF) {
            @Override
            void recv(String message) {
                json = message
                latch.countDown()
            }
        }
        Container container = new Container()
        container.text = TEXT
        Data data = new Data(Uri.LIF, container)
        Message message = new Message().body(data.asJson()).route(c.BOX_NLP_STANFORD, SELF)
        assert message.route.size() == 2
        po.send(message)

        if (!latch.await(30, TimeUnit.SECONDS)) {
            println 'No response from nlp service'
        }

        assert json != null
        Message msg = Serializer.parse(json, Message)
        data = Serializer.parse(msg.body, DataContainer)
        assert Uri.LIF == data.discriminator
        assert 2 == data.payload.views.size()

        println Serializer.toPrettyJson(data.payload)

//        def map = Serializer(msg.body, Data)
    }
}
