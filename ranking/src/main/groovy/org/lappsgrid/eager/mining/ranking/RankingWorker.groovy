package org.lappsgrid.eager.mining.ranking

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.ranking.model.Document
import java.util.concurrent.Callable

class RankingWorker implements Callable<Document> {

    Document document
    CompositeRankingEngine engines
    Query query

    RankingWorker(Document document, CompositeRankingEngine engines, Query query) {
        this.document = document
        this.engines = engines
        this.query = query
    }

    Document call() {
        return score(document, engines, query)
    }

    public Document score(Document document, CompositeRankingEngine engines, Query query) {
        return engines.rank2(query, document)
    }
}
