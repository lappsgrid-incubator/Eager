package org.lappsgrid.eager.mining.web.nlp

import org.apache.solr.common.SolrDocument
import org.lappsgrid.eager.core.solr.Fields
import org.lappsgrid.eager.mining.model.Document
import org.lappsgrid.eager.mining.nlp.Stanford
import org.lappsgrid.serialization.lif.Container

import java.util.concurrent.Callable

/**
 *
 */
class DocumentWorker implements Callable<Document> {

    SolrDocument solr
    Stanford nlp

    DocumentWorker(SolrDocument document, Stanford nlp) {
        this.solr = document
        this.nlp = nlp
    }

    Document call() {
        Document document = new Document()

        ['id', 'pmid','pmc','doi','year','path'].each { field ->
            document.setProperty(field, solr.getFieldValue(field))
        }

//        String temp = document.getFieldValue(Fields.TITLE)
//        if (temp) {
//            title = temp.toLowerCase()
//        }
//
//        intro = document.getFieldValue(Fields.INTRO)
//        articleAbstract = document.getFieldValue(Fields.ABSTRACT)
//        discussion = document.getFieldValue(Fields.DISCUSSION)

        document.title = process(Fields.TITLE)
        document.articleAbstract = process(Fields.ABSTRACT)
        return document
    }

    public Container process(String fieldName) {
        return nlp.process(solr.getFieldValue(fieldName))
    }
}
