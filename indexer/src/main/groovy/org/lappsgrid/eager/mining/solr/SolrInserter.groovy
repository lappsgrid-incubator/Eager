package org.lappsgrid.eager.mining.solr

import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.response.UpdateResponse
import org.lappsgrid.eager.core.solr.LappsDocument
import org.lappsgrid.eager.mining.DummyLogger
import org.lappsgrid.eager.mining.api.Sink

import java.util.concurrent.BlockingQueue

/**
 *
 */
//@Slf4j("logger")
class SolrInserter extends Sink {

    public static final int COMMIT_INTERVAL = 100
//    public static final String SOLR_ADDRESS = "http://149.165.169.127"
//    public static final String SOLR_ADDRESS = "http://solr1.lappsgrid.org"

    public static final int SOLR_PORT = 8983
    public static final String COLLECTION = "eager"

    public static final List SERVERS = [1,2].collect { "http://solr${it}.lappsgrid.org:8983/solr".toString() }
//    SolrClient solr
    CloudSolrClient solr
//    HttpSolrClient solr
    int interval

    int count = 0

    private String username
    private String password

    DummyLogger logger = new DummyLogger()

    public SolrInserter(BlockingQueue<Object> input) {
        this(COLLECTION, COMMIT_INTERVAL, input)
    }

    public SolrInserter(String core, BlockingQueue<Object> input) {
        this(core, COMMIT_INTERVAL, input)
    }

    public SolrInserter(String core, int interval, BlockingQueue<Object> input) {
        super("Inserter", input)
        this.interval = interval
//        solr = new HttpSolrClient.Builder("/solr/$core").build();
//        List servers = [ "http://localhost:8983/solr" ]

        solr = new CloudSolrClient.Builder(SERVERS).build()
//        solr = HttpSolrClient.Builder('http://solr1.lappsgrid.org:8983/solr').build()
        solr.setDefaultCollection(core)

        init()
    }

    private void init() {
        username = System.getenv("SOLR_USER")
        if (username == null) {
            username = "solr"
        }
        logger.debug("Solr username: {}", username)
        password = System.getenv("SOLR_PASS")
        if (password == null) {
            // This is guaranteed to fail with a 401/403 error, but that is better than than a NPE.
            logger.warn("Using default Solr password. This is will definitely fail later.")
            password = "SolrRocks"
        }
    }

    void store(Object item) {
        logger.trace("{} {} storing an item", ++count, name)
        LappsDocument document = (LappsDocument) item
//        UpdateRequest request = new UpdateRequest()
//        request.setBasicAuthCredentials(username, password)
//        request.add(document.solr())
        UpdateResponse response = solr.add(document.solr())
//        UpdateResponse response = request.process(solr)
        logger.trace("Indexed {}", response)
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