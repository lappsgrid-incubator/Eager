package org.lappsgrid.eager.mining.ranking

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.ranking.model.Document
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.ranking.model.Document

import java.awt.Composite
import java.util.concurrent.Callable

class RankingWorker implements Callable<Document> {

    //given document and compositerankingengine
    // for each engine in engines
    //calculate score and update document score fields
    //return document

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
            rank(document, engine, query)
        }
        return document
    }
    public void rank(Document document, RankingEngine engine, Query query) {
        engine.scoreDocument(query, document)
    }
}
