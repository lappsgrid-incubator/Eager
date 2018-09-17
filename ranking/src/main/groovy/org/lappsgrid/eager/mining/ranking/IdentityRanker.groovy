package org.lappsgrid.eager.mining.ranking

import org.lappgrid.eager.core.solr.LappsDocument
import org.lappsgrid.eager.mining.api.Ranker

/**
 *
 */
class IdentityRanker implements Ranker {
    @Override
    List<LappsDocument> rank(List<LappsDocument> documents) {
        return documents
    }
}
