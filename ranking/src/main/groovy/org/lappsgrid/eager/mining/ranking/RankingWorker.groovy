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
        engines.each {RankingEngine engine ->
            float result = 0.0f
            result += score(document, engine, query)
            document.score += result
        }
        return document
    }

    public float score(Document document, RankingEngine engine, Query query) {
        return engine.scoreDocument(query, document)
    }
}
