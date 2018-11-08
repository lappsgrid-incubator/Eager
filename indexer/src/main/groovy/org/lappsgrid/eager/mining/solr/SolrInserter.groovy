package org.lappsgrid.eager.mining.solr

import com.codahale.metrics.Meter
import com.codahale.metrics.Timer
import groovy.util.logging.Slf4j
import org.apache.solr.client.solrj.impl.CloudSolrClient
import org.apache.solr.client.solrj.response.UpdateResponse
import org.lappsgrid.eager.core.jmx.Registry
import org.lappsgrid.eager.core.solr.Fields
import org.lappsgrid.eager.core.solr.LappsDocument
import org.lappsgrid.eager.mining.DummyLogger
import org.lappsgrid.eager.mining.api.Sink

import java.util.concurrent.BlockingQueue

/**
 *
 */
@Slf4j("logger")
class SolrInserter extends Sink {

    public static final int COMMIT_INTERVAL = 100
    public static final String COLLECTION = "eager"

    public static final List SERVERS = [1,2].collect { "http://solr${it}.lappsgrid.org:8983/solr".toString() }

    final Meter documents = Registry.meter("solr.documents")
    final Meter errors = Registry.meter("solr.errors")
    final Timer timer = Registry.timer("solr.timer")

    CloudSolrClient solr
    int interval

    int count = 0

    private String username
    private String password

    public SolrInserter(BlockingQueue<Object> input) {
        this(COLLECTION, COMMIT_INTERVAL, input)
    }

    public SolrInserter(String core, BlockingQueue<Object> input) {
        this(core, COMMIT_INTERVAL, input)
    }

    public SolrInserter(String core, int interval, BlockingQueue<Object> input) {
        super("SolrInserter", input)
        this.interval = interval
        solr = new CloudSolrClient.Builder(SERVERS).build()
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
            // This is almost certain to fail with a 401/403 error, but that is better than than a NPE.
            logger.warn("Using default Solr password. This is will likely fail later.")
            password = "SolrRocks"
        }
    }

    void store(Object item) {
        documents.mark()
        LappsDocument document = (LappsDocument) item
        logger.info("{} storing {}", ++count, document.document.getFieldValue(Fields.ID))
        Timer.Context context = timer.time()
        try {
            UpdateResponse response = solr.add(document.solr())
            logger.debug("Indexed {}", response)
            if (count % interval == 0) {
                solr.commit()
            }
        }
        catch (Exception e) {
            logger.error("Unable to save document", e)
            errors.mark()
        }
        finally {
            context.stop()
        }
    }

    void finish() {
        logger.info("Finished.")
        solr.commit()
    }
}
