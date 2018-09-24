package org.lappsgrid.eager.web

import org.apache.solr.client.solrj.SolrClient
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.response.QueryResponse
import org.apache.solr.common.SolrDocumentList
import org.apache.solr.common.params.MapSolrParams
import org.junit.Ignore
import org.junit.Test

/**
 *
 */
@Ignore
class SolrTest {
    private String url = "http://149.165.169.127:8983/solr"

    @Test
    void body() {
        SolrClient solr = new CloudSolrClient.Builder(["http://149.165.169.127:8983/solr"]).build();
        String query = "body:breast AND body:cancer"
        Map params = [:]
        params.q = query
        params.fl = 'pmid,pmc,doi,year,title,path'
        params.rows = '10000'

        MapSolrParams queryParams = new MapSolrParams(params)

        final QueryResponse response = solr.query('pubmed', queryParams);
        final SolrDocumentList documents = response.getResults();
        println "Result size: ${documents.size()}"
    }

    @Test
    void url() {
        URL url = new URL("http://149.165.169.127:8983/solr/pubmed/select?q=breast%20AND%20cancer")
        println url.text
    }
}
