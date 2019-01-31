/*
 * This script assumes the rabbitmq module is already available in your local
 * Maven repository as it is not available on any public Maven repository.
 *
 * Run `mvn install` in the rabbitmq project to install it into your local Maven repo.
 */
import groovy.json.JsonOutput
@Grab('org.lappsgrid:discriminator:2.3.3')
import org.lappsgrid.discriminator.Discriminators
@Grab('org.lappsgrid.eager.mining:rabbitmq:1.1.0')
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.*

@Grab('org.lappsgrid:serialization:2.6.0')
import org.lappsgrid.serialization.Data
import org.lappsgrid.serialization.lif.Container

// Object used for synchronization.
Object semaphore = new Object()

// The name of the mailbox we will use. We should take better care to ensure that
// we use a name that is not already in use.
String MY_MAILBOX = 'return-address'

println "Registering a message box"
MessageBox box = new MessageBox('eager.postoffice', MY_MAILBOX) {
    void recv(Message message) {
        println JsonOutput.prettyPrint(message.body)
        synchronized (semaphore) {
            semaphore.notifyAll()
        }
    }
}

// Prepare the message to send
println "Preparing the message"
Container container = new Container()
container.text = 'Karen flew to New York.'
container.language = 'en'
Data data = new Data(Discriminators.Uri.LIF, container)

Message message = new Message()
                    .body(data.asJson())
                    .route('nlp.stanford', MY_MAILBOX)

// Send the message to the post office.
println "Sending the message"
PostOffice po = new PostOffice('eager.postoffice')
po.send(message)

// And wait for the handler above to notify us.
println "Waiting for the reply"
synchronized (semaphore) {
    semaphore.wait()
}

println "Shutting down"
box.close()
po.close()
println "Done."
