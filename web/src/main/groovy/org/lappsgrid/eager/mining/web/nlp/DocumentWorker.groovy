package org.lappsgrid.eager.mining.web.nlp

import org.apache.solr.common.SolrDocument
import org.lappsgrid.eager.mining.core.solr.Fields
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.ranking.model.Document

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

        document.title = process(Fields.TITLE)
        document.articleAbstract = process(Fields.ABSTRACT)
        document.body = solr.getFieldValue(Fields.BODY)
        return document
    }

    public Section process(String fieldName) {
        Object text = solr.getFieldValue(fieldName)
        if (text == null) {
            Section section = new Section()
            section.text = ''
            return section
        }
        return nlp.process(text.toString())
    }

//    public Container process(String fieldName) {
//        return nlp.process(solr.getFieldValue(fieldName))
//    }
}
