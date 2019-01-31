import groovy.json.JsonOutput
@Grab('org.lappsgrid:discriminator:2.3.3')
import org.lappsgrid.discriminator.Discriminators
@Grab('org.lappsgrid.eager.mining:rabbitmq:1.1.0')
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.*

@Grab('org.lappsgrid:serialization:2.6.0')
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.lif.Container

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

final String EXCHANGE = 'example.postoffice'
PostOffice po = new PostOffice(EXCHANGE)

// Set up several "workers"
List<MessageBox> boxes = ['a', 'b', 'c', 'd', 'e'].collect{ id -> new MessageBox(EXCHANGE, id) {
    void recv(Message message) {
        message.body += id
        po.send(message)
    }
}}

CountDownLatch latch = new CountDownLatch(4)
boxes << new MessageBox(EXCHANGE, 'final') {
    void recv(Message message) {
        println message.body
        latch.countDown()
    }
}

// Send the message to the post office.
println "Sending the messages"
po.send(message('abcde'))
po.send(message('bad'))
po.send(message('baabaadaa'))
po.send(message('dabe'))

latch.await(30, TimeUnit.SECONDS)

println "Shutting down"
boxes*.close()
po.close()
println "Done."
return

Message message(String route) {
    Message m = new Message()
    m.body('start -> ')
    route.each {
        m.route(new String(it))
    }
    m.route('final')
    return m
}