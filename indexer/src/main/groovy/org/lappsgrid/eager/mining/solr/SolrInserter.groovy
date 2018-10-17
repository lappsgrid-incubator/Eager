package org.lappsgrid.eager.mining.solr

import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.response.UpdateResponse
import org.lappsgrid.eager.core.solr.LappsDocument
import org.lappsgrid.eager.mining.api.Sink

import java.util.concurrent.BlockingQueue

/**
 *
 */
class SolrInserter extends Sink {

    public static final int COMMIT_INTERVAL = 100
    public static final String SOLR_ADDRESS = "http://149.165.169.127"
    public static final int SOLR_PORT = 8983

//    SolrClient solr
    CloudSolrClient solr
    int interval

    int count = 0

    public SolrInserter(BlockingQueue<Object> input) {
        this('pubmed', COMMIT_INTERVAL, input)
    }

    public SolrInserter(String core, BlockingQueue<Object> input) {
        this(core, COMMIT_INTERVAL, input)
    }

    public SolrInserter(String core, int interval, BlockingQueue<Object> input) {
        super("Inserter", input)
        this.interval = interval
//        solr = new HttpSolrClient.Builder("/solr/$core").build();
        List servers = [ "http://localhost:8983/solr" ]
        solr = new CloudSolrClient.Builder(servers).build()
        solr.setDefaultCollection(core)
    }

    void store(Object item) {
        println "${++count} $name storing an item"
        LappsDocument document = (LappsDocument) item
        UpdateResponse response = solr.add(document.solr())
        println "Indexed " + response.toString()
        if (count % interval == 0) {
            solr.commit()
        }

    }
}
/*
class Inserter extends Haltable {

    BlockingQueue<SolrInputDocument> q;

    public Inserter(BlockingQueue<SolrInputDocument> q) {
        this.q = q
    }

    void run() {
        println "Solr update thread starting"
        SolrClient solr = new HttpSolrClient.Builder('http://localhost:8983/solr/pmc').build();
        int count = 0
        int killed = 0
        running = true
        while (running) {
            try {
                SolrInputDocument document = q.take()
                if (document == Parser.DONE) {
                    ++killed
                    if (killed == 2) {
                        running = false
                    }
                    continue
                }
                UpdateResponse response = solr.add(document)
                println "Inserter: " + response.toString()
//                println "Inserter: added document ${document.getField('pmid')}"
                if (++count % 100 == 0) {
                    solr.commit()
                }
            }
            catch (InterruptedException e) {
                println "Solr update thread has been interrupted."
                running = false
                Thread.currentThread().interrupt()
            }
        }
        solr.commit()
        println "Solr update thread terminated."
        println "$count documents indexed."
    }
}
*/