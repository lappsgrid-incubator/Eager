package org.lappsgrid.eager.model

import org.apache.solr.common.SolrDocument

/**
 *
 */
class Document {
    String pmid
    String pmc
    String doi
    String year
    String title
    String path
    float score
    Map<String,Float> scores

    /*
                d.pmid = doc.getFieldValue('pmid')
            d.pmc = doc.getFieldValue('pmc')
            d.doi = doc.getFieldValue('doi')
            d.year = doc.getFieldValue('year')
            d.title = doc.getFieldValue('title')
            d.path = doc.getFieldValue('path')

     */

    Document() {
        score = 0.0f
        scores = [:]
    }

    Document(SolrDocument document) {
        ['pmid','pmc','doi','year','title','path'].each { field ->
            this.setProperty(field, document.getFieldValue(field))
        }
        score = 0.0f
        scores = [:]
    }
}
