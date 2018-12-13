package org.lappsgrid.eager.web

import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrDocumentList
import org.apache.solr.common.params.MapSolrParams
import org.lappsgrid.eager.core.Configuration
import org.lappsgrid.eager.core.Factory
import org.lappsgrid.eager.core.json.Serializer
import org.lappsgrid.eager.core.ssl.SSL
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.api.QueryProcessor
import org.lappsgrid.eager.mining.model.Document
import org.lappsgrid.eager.mining.model.GDDDocument
import org.lappsgrid.eager.mining.ranking.CompositeRankingEngine
import org.lappsgrid.eager.mining.ranking.RankingEngine
import org.lappsgrid.eager.query.SimpleQueryProcessor
import org.lappsgrid.eager.query.elasticsearch.ESQueryProcessor
import org.lappsgrid.eager.query.elasticsearch.GDDSnippetQueryProcessor
import org.lappsgrid.eager.rabbitmq.Message
import org.lappsgrid.eager.rabbitmq.topic.MailBox
import org.lappsgrid.eager.rabbitmq.topic.PostOffice
import org.lappsgrid.eager.service.Version
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.context.request.WebRequest

/**
 *
 */
@Slf4j("logger")
@Controller
@ControllerAdvice
class AskController {

    private static final Configuration c = new Configuration()

    QueryProcessor queryProcessor
    QueryProcessor geoProcessor
    String collection

    public AskController() {
        queryProcessor = new SimpleQueryProcessor()
        geoProcessor = new GDDSnippetQueryProcessor()
        collection = "bioqa"
//        collection = "eager"
        SSL.enable()
    }

    @GetMapping(path="/show", produces = ['text/html'])
    @ResponseBody String show(@RequestParam String path) {
        String body = "<body><h1>Error</h1><p>An error occured retrieving $path</p></body>"
        String xml = fetch(path)
        if (xml) {
            body = transform(xml)
        }
        return """
<html>
    <head>
        <title>$path</title>
        <style>
            body {
                font-size: 12pt;
                margin: 10em;
            }
        </style>
    </head>
    $body
<html>
"""
    }

    /*
                        td 'Number of consecutive terms in title'
                        td 'Total number of search terms in title'
                        td 'Term position in title, earlier in the text == better score'
                        td 'Words in the title that are search terms'
     */
    @GetMapping(path = "/ask", produces = ['text/html'])
    String get(Model model) {
        logger.info("GET /ask")
        updateModel(model)
        List<String> descriptions = [
                "consecutive terms",
                "total search terms",
                "position",
                "% search terms"
        ]
        model.addAttribute("descriptions", descriptions)
        logger.debug("Rendering mainpage")
        return "mainpage"
    }

    /*
    @PostMapping(path = "/ask", produces = 'application/json')
    @ResponseBody String post(@RequestParam String question) {
        return Serializer.toPrettyJson(answer(question))
    }


    @PostMapping(path = "/ask", produces = 'text/html')
    String postHtml(@RequestParam String question, Model model) {
        Map reply = answer(question)
        model.addAttribute('data', reply)
        return 'answer'
    }
    */

    @PostMapping(path="/question", produces="text/html")
    String postHtml(@RequestParam Map<String,String> params, Model model) {
        logger.info("POST /question")
        updateModel(model)
//        if (true) {
//            model.addAttribute("params", params)
//            return "dump"
//        }
        if (params.domain == 'geo') {
            //TODO Check that a question has been entered
            model.addAttribute('data', geodeepdive(params, 1000))
            logger.debug("Rendering geodd")
            return 'geodd'
        }

        Map reply = answer(params, 100)
        model.addAttribute('data', reply)
        logger.debug("Rendering data")
        return 'answer'
    }

    private String elasticsearch(String question) {
        return new ESQueryProcessor().transform(question)
    }

    private Map answer(Map params) {
        return answer(params, 100)
    }

    private Map answer(Map params, int size) {
//        SolrClient solr = new CloudSolrClient.Builder(["http://149.165.169.127:8983/solr"]).build();
        logger.debug("Generating answer.")

        logger.trace("Creating CloudSolrClient")
        SolrClient solr = new CloudSolrClient.Builder(["http://129.114.16.34:8983/solr"]).build();
        logger.trace("Generating query")
        Query query = queryProcessor.transform(params.question)
        Map solrParams = [:]
        solrParams.q = query.query
        solrParams.fl = 'pmid,pmc,doi,year,title,path,abstract'
        solrParams.rows = '10000'

        MapSolrParams queryParams = new MapSolrParams(solrParams)

        logger.trace("Sending query to Solr")
        final QueryResponse response = solr.query(collection, queryParams);
        final SolrDocumentList documents = response.getResults();

        int n = documents.size()
        logger.trace("Received {} documents", n)
        Map result = [:]
        result.query = query
        result.size = n

        List docs = []
        for (int i = 0; i < n; ++i) {
            SolrDocument doc = documents.get(i)
             docs << new Document(doc)
        }

        result.documents = rank(query, docs, params)
        if (result.documents.size() > size) {
            logger.debug("Trimming results to {}", size)
            result.documents = result.documents[0..size]
        }
        if (result.documents.size() > 0) {
            Document exemplar = result.documents[0]
            result.keys = exemplar.scores.keySet()
        }
        return result
    }

    private List rank(Query query, List<Document> documents, Map params) {
//        RankingEngine ranker = new RankingEngine(params)
//        return ranker.rank(query, documents)
        logger.debug("Ranking {} documents", documents.size())
        CompositeRankingEngine ranker = new CompositeRankingEngine(params)
        return ranker.rank(query, documents)
    }

    private List rank(Query query, List<Document> documents, Map params, Closure getter) {
        logger.debug("Ranking {} documents", documents.size())
        RankingEngine ranker = new RankingEngine(params)
        return ranker.rank(query, documents, getter)
    }

    private String transform(String xml) {
        XmlParser parser = Factory.createXmlParser()

        def article = parser.parseText(xml)

//        transformations.sec = { div([:], null) }
//        transformations.italic = { em([:], null) }
//        transformations.xref = { strong([:], null) }
//        transformations.title = { title([:], null) }
        Node body = article.body[0]
        if (body == null) {
            return xml
        }

        List<Node> dfs = body.depthFirst()
        replaceAll(dfs, 'sec', 'div')
        replaceAll(dfs, 'italic', 'em')
        replaceAll(dfs, 'xref', 'strong')
        replaceAll(dfs, 'title', 'h1')

//        Node html = new Node(null, 'html')
//        Node head = new Node(html, 'head')
//        new Node(head, 'title', 'PMC')
//        html.append(body)

        StringWriter writer = new StringWriter()
        XmlNodePrinter printer = new XmlNodePrinter(new PrintWriter(writer))
        printer.print(body)
        return writer.toString()
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

    String fetch(String path) {
        Object lock = new Object()
        String returnAddress = UUID.randomUUID().toString()
        PostOffice po = new PostOffice(c.POSTOFFICE)
        String xml = null //'<body><h1>Error</h1><p>There was a problem loading the document content.</p></body>'
        MailBox box = new MailBox(c.POSTOFFICE, returnAddress) {
            void recv(String json) {
                try {
                    Message message = Serializer.parse(json, Message)
                    xml = message.body
                    if (message.command == 'loaded') {
                        xml = message.body
                    }
                    else {
                        println "ERROR: ${message.command}"
                        println "BODY: " + message.body
                    }
                }
                catch (Exception e) {
                    e.printStackTrace()
                    throw e
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

        po.send(message)
        synchronized (lock) {
            lock.wait(4000)
        }
        po.close()
        box.close()
        return xml
    }

    Map geodeepdive(Map params, int size) {
        println "GeoDeepDive limit: $size"
        geoProcessor.limit = size
        Query query = geoProcessor.transform(params.question)
        println "Query: ${query.query}"
        println "Terms: ${query.terms.join", "}"
        String json = new URL(query.query).text
        Map data = new JsonSlurper().parseText(json)
        if (data.size() == 0) {
            return null
        }

        List<Document> docs = []
        data.success.data.each { record ->
            GDDDocument doc = new GDDDocument()
            doc.title = record.title
            doc.highlight = record.highlight
            doc.hits = record.hits
            doc.doi = record.doi
            doc.year = record.coverDate
            docs.add(doc)
        }

        Map result = [:]
        result.query = query
        result.size = docs.size()

        List<Document> ranked = rank(query, docs, params, { it.highlight })
        if (ranked.size() > size) {
            result.documents = ranked[0..size]
        }
        else {
            result.documents = ranked
        }
        if (result.documents.size() > 0) {
            Document exemplar = result.documents[0]
            result.keys = exemplar.scores?.keySet()
        }
        return result

    }

    private void updateModel(Model model) {
        model.addAttribute('version', Version.version)
    }

    @ExceptionHandler(Exception.class)
    protected String handleAddExceptions(Exception ex, WebRequest request) {
        logger.error("Caught an exception", ex)
    }
}
