package org.lappsgrid.eager.mining.retrieval

import groovy.util.slurpersupport.NodeChild
import groovy.xml.XmlUtil
import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.core.Factory
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice

/**
 * The IntegrationTest expects a DocumentLoader to have been started already.
 */
@Ignore
class IntegrationTest {

    Map<String,Closure> transformations = [:]

    @Test
    void load() {
        String mbox = 'test.integration.load'
        boolean received = false
        String body = 'Hello world.'
        File tmpFile = File.createTempFile('eager', '.txt', new File('/tmp'))
        tmpFile.text = body

        Configuration c = new Configuration()
        PostOffice po = new PostOffice(c.POSTOFFICE)
        MailBox box = new MailBox(c.POSTOFFICE, mbox) {
            @Override
            void recv(String json) {
                println "Received $json"
                Message message = Serializer.parse(json, Message)
                println "Message: ${message.body}"
                assert body == message.body
                received = true
            }
        }

        Message message = new Message()
                .command('load')
                .body(tmpFile.path)
                .route('load')
                .route(mbox)

        po.send(message)
        sleep(2000)
        tmpFile.delete()
        po.send(c.BOX_LOAD, 'shutdown')
        assert received
    }

    @Test
    void pmc() {
        String mbox = 'integration.load'
        boolean received = false
        String id = '/var/data/pmc/xml/non/Br_J_Cancer/PMC2376180.nxml'

        Configuration c = new Configuration()
//        String xml = rpc(c.POSTOFFICE, id)
//        println xml

        PostOffice po = new PostOffice(c.POSTOFFICE)
        MailBox box = new MailBox(c.POSTOFFICE, mbox) {
            @Override
            void recv(String json) {
                println "Received $json"
                Message message = Serializer.parse(json, Message)
                println "Message: ${message.body}"
                received = true
            }
        }
        Message message = new Message()
                .command('load')
                .body(id)
                .route('load')
                .route(mbox)

        po.send(message)
        sleep(2000)
//        po.send(c.BOX_LOAD, 'shutdown')
        assert received

    }

    @Test
    void pmc2() {
        String id = '/var/data/pmc/xml/non/Br_J_Cancer/PMC2376180.nxml'

        Configuration c = new Configuration()
        String xml = rpc(c.POSTOFFICE, id)
        assert xml
//        File file = new File('/tmp/example.xml')
        XmlParser parser = Factory.createXmlParser()
        Node node = parser.parseText(xml)
        println XmlUtil.serialize(node)

    }

    @Test
    void xml() {
        String xml = new File('/tmp/example.xml').text
        XmlParser parser = Factory.createXmlParser()
//        XmlSlurper parser = Factory.createXmlSlurper()

        def article = parser.parseText(xml)
        assert article

//        article.depthFirst().findAll { it.name() == 'sec' }.each { Node node ->
//            println node.name()
//        }
        transformations.sec = { div([:], null) }
        transformations.italic = { em([:], null) }
        transformations.xref = { strong([:], null) }
        transformations.title = { title([:], null) }
        Node body = article.body[0]

        List<Node> dfs = body.depthFirst()
        replaceAll(dfs, 'sec', 'div')
        replaceAll(dfs, 'italic', 'em')
        replaceAll(dfs, 'xref', 'strong')
        replaceAll(dfs, 'title', 'h1')

        Node html = new Node(null, 'html')
        Node head = new Node(html, 'head')
        new Node(head, 'title', 'PMC')
        html.append(body)

        StringWriter writer = new StringWriter()
        XmlNodePrinter printer = new XmlNodePrinter(new PrintWriter(writer))
//        printer.setPreserveWhitespace(true)
        printer.print(html)
        new File('/tmp/example.html').text = writer.toString()
//        new XmlNodePrinter(preserveWhitespace:true).print(html)
    }

    void replaceAll(List<Node> nodes, String from, String to) {
        List list = nodes.findAll { it instanceof Node && it.name() == from }
        list.each { Node node ->
            Node newNode = new Node(null, to)
            node.children().each { add(newNode, it) }
            node.replaceNode(newNode)
        }
    }

    void add(Node node, Node child) {
        node.append(child)
    }
    void add(Node node, String value) {
        node.value = value
    }

    void transform(Node node) {
        println "transforming ${node.name()}"
//        Closure newNode = transformations[node.name()]
//        if (newNode) {
//            println "Replacing ${node.name()}"
//            node.replaceNode(newNode)
//        }
        if (node.name() == 'sec') {
//            List children = node.children()
            node.replaceNode { new Node(node.parent(), 'div', null) }
//            children.each { node.append(it) }
        }
        node.children().each { transform it }
//        return node
    }

    void transform(String text) { }

    String rpc(String exchange, String path) {
        Object lock = new Object()
        String returnAddress = UUID.randomUUID().toString()
        PostOffice po = new PostOffice(exchange)
        String xml = null
        MailBox box = new MailBox(exchange, returnAddress) {
            void recv(String json) {
                try {
                    println "Received JSON"
                    Message message = Serializer.parse(json, Message)
                    println 'parsed message'
                    if (message.command == 'loaded') {
                        println 'document was loaded'
                        xml = message.body
                    }
                    else {
                        println "ERROR: ${message.command}"
                        println "BODY: " + message.body
                    }
                }
                finally {
                    synchronized (lock) {
                        lock.notifyAll()
                    }
                }
            }
        }

        Message message = new Message()
                .command('load')
                .body(path)
                .route('load')
                .route(returnAddress)
        println 'sending message'
        po.send(message)
        println 'waiting'
        synchronized (lock) {
            lock.wait(2000)
        }
        if (xml) {
            println 'xml was returned'
        }
        else {
            println 'returning null'
        }
        return xml
    }
}
