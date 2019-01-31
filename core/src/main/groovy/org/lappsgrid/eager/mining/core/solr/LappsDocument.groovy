package org.lappsgrid.eager.mining.core.solr

import org.apache.solr.common.SolrInputDocument

/**
 * Manages a SolrDocument instance and provides a fluent API for initialization.
 */
class LappsDocument {

    public static final class Type {
        public static final String PMC = "pmc"
        public static final String PUBMED = "pubmed"
    }

    SolrInputDocument document

    public LappsDocument() {
        document = new SolrInputDocument()
    }

    public LappsDocument(Map map) {
        map.each { key,value ->
            document.addField(key, value)
        }
    }

    SolrInputDocument solr() { return document }

    LappsDocument id(String id) {
        if (id == null || id == 'null') {
//            println "WARNING: ID is null. Generating a UUID"
            id = UUID.randomUUID().toString()
        }
        add(Fields.ID, id)
    }

    LappsDocument pmid(String id) {
        add(Fields.PMID, id)
    }

    LappsDocument pmc(String id) {
        add(Fields.PMC, id)
    }

    LappsDocument doi(String id) {
        add(Fields.DOI, id)
    }

    LappsDocument title(String title) {
        add(Fields.TITLE, title)
    }

    LappsDocument year(int year) {
        add(Fields.YEAR, Integer.toString(year))
    }

    LappsDocument year(String year) {
        add(Fields.YEAR, year)
    }

    LappsDocument theAbstract(String theAbstract) {
        add(Fields.ABSTRACT, theAbstract)
    }
    LappsDocument articleAbstract(String articleAbstract) {
        add(Fields.ABSTRACT, articleAbstract)
    }

    LappsDocument intro(String intro) {
        add(Fields.INTRO, intro)
    }
    LappsDocument introduction(String intro) {
        add(Fields.INTRO, intro)
    }

    LappsDocument discussion(String discussion) {
        add(Fields.DISCUSSION, discussion)
    }

    LappsDocument body(String body) {
        add(Fields.BODY, body)
    }

    LappsDocument results(String results) {
        add(Fields.RESULTS, results)
    }

    LappsDocument path(String path) {
        add(Fields.PATH, path)
    }

    LappsDocument mesh(String mesh) {
        add(Fields.MESH, mesh)
    }

    LappsDocument keywords(String keywords) {
        add(Fields.KEYWORDS, keywords)
    }

    LappsDocument journal(String journal) {
        add(Fields.JOURNAL, journal)
    }

    LappsDocument type(String type) {
        add(Fields.TYPE, type)
    }

    LappsDocument pmc() {
        type(Type.PMC)
    }

    LappsDocument pubmed() {
        type(Type.PUBMED)
    }

    String getValue(String name) {
        return document.getFieldValue(name).toString()
    }

    Collection<Object> getValues(String name) {
        return document.getFieldValues(name)
    }

    private LappsDocument add(String name, String value) {
        document.setField(name, value)
        return this
    }
}
