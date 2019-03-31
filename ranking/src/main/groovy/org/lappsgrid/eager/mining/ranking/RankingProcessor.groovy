package org.lappsgrid.eager.mining.ranking

import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrDocumentList
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.ranking.model.Document

import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class RankingProcessor {

    ExecutorCompletionService<Document> executor
    CompositeRankingEngine engines

//  DocumentProcessor() {
//      this(Runtime.getRuntime().availableProcessors())
//  }

    RankingProcessor(int nThreads, Map params) {
        executor = new ExecutorCompletionService<>(Executors.newFixedThreadPool(nThreads))
        engines = new CompositeRankingEngine(params)
    }

    List<Document> rank(Query query, List<Document> documents) {
        List<Document> result = new ArrayList<>()
        List<Future<Document>> futures = new ArrayList<>()

        for (int i = 0; i < documents.size(); ++i) {
            Document document = documents.get(i)
            Future<Document> future = executor.submit(new RankingWorker(document, engines, query))
            futures.add(future)
        }

        int count = 0
        while (count < documents.size()) {
            ++count
            Future<Document> future = executor.take()
            try {
                Document document = future.get()
                result.add(document)
            }
            catch (Throwable e) {
                logger.error("Unable to get future document.", e)
//                throw new IOException(e)
            }
        }
        return result
    }



}
