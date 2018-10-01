package org.lappsgrid.eager.web

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrDocumentList
import org.apache.solr.common.params.MapSolrParams
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.api.QueryProcessor
import org.lappsgrid.eager.model.Document
import org.lappsgrid.eager.query.SimpleQueryProcessor
import org.lappsgrid.eager.rank.RankingEngine
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

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

    @PostMapping(path="/ask", produces="text/html")
    String postHtml(@RequestParam Map<String,String> params, Model model) {
//        model.addAttribute('params', params)
//        return 'test'
        Map reply = answer(params, 1000)
        model.addAttribute('data', reply)
        return 'answer'
    }

    private Map answer(Map params) {
        return answer(params, 100)
    }

    private Map answer(Map params, int size) {
        SolrClient solr = new CloudSolrClient.Builder(["http://149.165.169.127:8983/solr"]).build();
        Query query = queryProcessor.transform(params.question)
        Map solrParams = [:]
        solrParams.q = query.query
        solrParams.fl = 'pmid,pmc,doi,year,title,path'
        solrParams.rows = '10000'

        MapSolrParams queryParams = new MapSolrParams(solrParams)

        final QueryResponse response = solr.query(collection, queryParams);
        final SolrDocumentList documents = response.getResults();

        int n = documents.size()
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
            result.documents = docs[0..size]
        }
        return result
    }

    private List rank(Query query, List<Document> documents, Map params) {
        RankingEngine ranker = new RankingEngine(params)
        return ranker.rank(query, documents)
    }
}
