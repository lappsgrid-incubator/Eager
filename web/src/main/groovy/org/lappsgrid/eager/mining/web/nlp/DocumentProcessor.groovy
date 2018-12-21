package org.lappsgrid.eager.mining.web.nlp

import groovy.util.logging.Slf4j
import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrDocumentList
import org.lappsgrid.eager.mining.model.Document

import java.util.concurrent.ExecutorCompletionService
import java.util.concurrent.Executors
import java.util.concurrent.Future

/**
 *
 */
@Slf4j('logger')
class DocumentProcessor {

    ExecutorCompletionService<Document> executor
    Stanford nlp

    DocumentProcessor() {
        this(Runtime.getRuntime().availableProcessors())
    }

    DocumentProcessor(int nThreads) {
        executor = new ExecutorCompletionService<>(Executors.newFixedThreadPool(nThreads))
        nlp = new Stanford()
    }

    List<Document> process(SolrDocumentList solrDocumentList) {
        List<Document> result = new ArrayList<>()
        List<Future<Document>> futures = new ArrayList<>()

        // Submit each Solr document to be processed in
        for (int i = 0; i < solrDocumentList.size(); ++i) {
            SolrDocument solrDoc = solrDocumentList.get(i)
            Future<Document> future = executor.submit(new DocumentWorker(solrDoc, nlp))
            futures.add(future)
        }

        int count = 0
        while (count < solrDocumentList.size()) {
            ++count
            Future<Document> future = executor.take()
            try {
                Document document = future.get()
                result.add(document)
            }
            catch (Exception e) {
                logger.error(e)
            }
        }
        return result
    }
}
