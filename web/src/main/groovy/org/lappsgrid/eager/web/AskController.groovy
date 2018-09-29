package org.lappsgrid.eager.web

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.impl.HttpSolrClient
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrDocumentList
import org.apache.solr.common.params.MapSolrParams
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.api.QueryProcessor
import org.lappsgrid.eager.model.Document
import org.lappsgrid.eager.query.SimpleQueryProcessor
import org.lappsgrid.serialization.Serializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody

/**
 *
 */
@Controller
class AskController {

    QueryProcessor queryProcessor
    String collection

    public AskController() {
        queryProcessor = new SimpleQueryProcessor()
        collection = "pubmed"
    }

    @GetMapping(path = "/ask", produces = ['text/html'])
    String get() {
        return "ask"
    }

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

    private Map answer(String question) {
        return answer(question, 10)
    }

    private Map answer(String question, int size) {
        SolrClient solr = new CloudSolrClient.Builder(["http://149.165.169.127:8983/solr"]).build();
        Query query = queryProcessor.transform(question)
        Map params = [:]
        params.q = query.query
        params.fl = 'pmid,pmc,doi,year,title,path'
        params.rows = '10000'

        MapSolrParams queryParams = new MapSolrParams(params)

        final QueryResponse response = solr.query(collection, queryParams);
        final SolrDocumentList documents = response.getResults();

        int n = documents.size()
        Map result = [:]
//        result.question = question
        result.query = query
        result.size = n

        if (n > size) {
            n = size
        }

        List docs = []
        for (int i = 0; i < n; ++i) {
            SolrDocument doc = documents.get(i)
            /*
            Map d = [:]
            d.pmid = doc.getFieldValue('pmid')
            d.pmc = doc.getFieldValue('pmc')
            d.doi = doc.getFieldValue('doi')
            d.year = doc.getFieldValue('year')
            d.title = doc.getFieldValue('title')
            d.path = doc.getFieldValue('path')
            */
            docs << new Document(doc)
        }
        result.documents = rank(query, docs)
        return result
    }

    private List rank(Query query, List<Document> documents) {
        documents.sort { a,b -> a.title.length() <=> b.title.length() }
    }
}
