package org.lappsgrid.eager.mining.web.nlp.stanford

import com.codahale.metrics.Timer
import com.codahale.metrics.Meter
import groovy.util.logging.Slf4j
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.serialization.DataContainer
import org.lappsgrid.serialization.Serializer

/**
 *
 */
@Slf4j("logger")
class Worker implements Runnable {

    Pipeline pipeline
    Message message
    PostOffice post

    Timer timer
    Meter counter
    Meter errors

    Worker(Pipeline pipeline, Message message, PostOffice post, Timer timer, Meter counter, Meter errors) {
        this.pipeline = pipeline
        this.message = message
        this.post = post
        this.timer = timer
        this.counter = counter
        this.errors = errors
        logger.debug("Worker created")
    }

    void run() {
        logger.info("Staring worker in thread {}", Thread.currentThread().name)
        DataContainer data
        Timer.Context context = timer.time()
        try {
            data = Serializer.parse(message.body, DataContainer)
            // TODO Check the discriminator
            data.payload = pipeline.process(data.payload)
            counter.mark()
        }
        catch (Exception e) {
            logger.error("Unable to process input.", e)
            errors.mark()
            return
        }
        finally {
            context.close()
        }
        logger.debug("Sending result to {}", message.route[0])
        message.body = data.asJson()
        post.send(message)
    }
}
