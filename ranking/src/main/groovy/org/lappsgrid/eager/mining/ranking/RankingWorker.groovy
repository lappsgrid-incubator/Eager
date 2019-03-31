package org.lappsgrid.eager.mining.ranking

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.core.solr.Fields
import org.lappsgrid.eager.mining.ranking.model.Document
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.ranking.model.Document
import java.util.concurrent.Callable

class RankingWorker implements Callable<Document> {

    //given document and compositerankingengine
    // for each engine in engines
        //calculate score and update document score fields
        //return document

    RankingWorker(Document document, CompositeRankingEngine engines, Query query) {
        this.document = document
        this.engines = engines
        this.query = query
    }

    Document call() {
        
    }
    public Section rank(Document document, RankingEngine engine, Query query) {

    }
}
