package org.lappsgrid.eager.mining.index

import com.codahale.metrics.Meter
import org.lappsgrid.eager.core.Factory
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.concurrent.CountDownLatch

import static org.lappsgrid.eager.mining.index.Main.registry
import static org.lappsgrid.eager.mining.index.Main.name

/**
 *
 */
class SectionParser implements Runnable {

    static final String MBOX = "section-parser"

    final Logger logger = LoggerFactory.getLogger(SectionParser)

    final Meter badRequests
    final Meter exceptions

    final XmlParser parser
    SectionParser() {
        badRequests = registry.meter(name("bad-requests"))
        exceptions = registry.meter(name("exceptions"))
        parser = Factory.createXmlParser()
    }

    void run() {
        CountDownLatch latch = new CountDownLatch(1)
        MailBox box = new MailBox(Main.POSTOFFICE, MBOX)  {
            void recv(String input) {
                try {
                    Message message = Serializer.parse(input, Message)
                    if (message.command == 'loaded') {
                        logger.debug("Parsing input size {}", message.body.size())
                        parse(message.body)
                    }
                    else if (message.command == 'shutdown') {
                        logger.info("Received a shutdown message")
                        latch.countDown()
                    }
                    else {
                        logger.warn("Received bad request: {}", message.command)
                        badRequests.mark()
                    }
                }
                catch (Exception e) {
                    logger.error("Error processing input", e)
                    exceptions.mark()
                }
            }
        }
        if (!latch.await()) {
            logger.error("There was a problem waiting for the SectionParser thread to terminate.")
        }
    }

    void parse(String xml) {
        Node article = parser.parseText(xml)
        article.body.sec.each { Node section ->
            String type = section.attribute('sec-type')
            if (type) {
                sections.add(type.trim().toLowerCase())
            }
        }
    }
}
