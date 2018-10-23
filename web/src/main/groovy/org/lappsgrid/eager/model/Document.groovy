package org.lappsgrid.eager.model

import org.apache.solr.common.SolrDocument
import org.lappsgrid.eager.core.solr.Fields

/**
 *
 */
class Document {
    String pmid
    String pmc
    String doi
    String year
    String title
    String articleAbstract
    String intro
    String discussion
    String results
    String path
    /** The total score for the document. */
    float score
    /** The scores for each section. */
    Map<String,Scores> scores

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
        intro = document.getFieldValue(Fields.INTRO)
        articleAbstract = document.getFieldValue(Fields.ABSTRACT)
        discussion = document.getFieldValue(Fields.DISCUSSION)
        score = 0.0f
        scores = [:]
    }

    void addScore(String section, String algorithm, float value) {
        Scores forSection = scores.get(section)
        if (forSection == null) {
            forSection = new Scores()
            scores.put(section, forSection)
        }
        forSection[algorithm] = value
    }
}
