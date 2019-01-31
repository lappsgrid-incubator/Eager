package org.lappsgrid.eager.mining.ranking

//import org.lappsgrid.eager.mining.core.solr.LappsDocument
import org.lappsgrid.eager.mining.api.Ranker

/**
 *
 */
class IdentityRanker implements Ranker {
    @Override
    List<Object> rank(List<Object> documents) {
        return documents
    }
}
