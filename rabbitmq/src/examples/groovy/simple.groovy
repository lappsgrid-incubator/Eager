@Grab('org.lappsgrid.eager.mining:rabbitmq:1.1.0')
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.*

import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

final String EXCHANGE = 'example.simple.postoffice'
PostOffice po = new PostOffice(EXCHANGE)

String a = 'a'
String b = 'b'
String x = 'x'

List routes = [
        [a, b, x],
        [a, a, b, b, x],
        [b, x],
        [a, x],
        [a, b, a, b, a, b, x],
        [b, a, a, b, a, a, x]
]

CountDownLatch latch = new CountDownLatch(routes.size())
MessageBox abox = new MessageBox(EXCHANGE, 'a') {
    @Override
    void recv(Message message) {
        message.body += 'a'
        po.send(message)
    }
}

MessageBox bbox = new MessageBox(EXCHANGE, 'b') {
    @Override
    void recv(Message message) {
        message.body += 'b'
        po.send(message)
    }
}

MessageBox xbox = new MessageBox(EXCHANGE, 'x') {
    @Override
    void recv(Message message) {
        println message.body
        latch.countDown()
    }
}

routes.each { route ->
    Message m = new Message()
    m.body('start -> ')
    route.each { m.route(it) }
    po.send(m)
}

// Wait at least 30 seconds for everything to complete.
latch.await(30, TimeUnit.SECONDS)

println "Shutting down"
abox.close()
bbox.close()
xbox.close()
po.close()
println "Done."
return

