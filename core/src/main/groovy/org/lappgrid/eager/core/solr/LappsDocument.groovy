package org.lappgrid.eager.core.solr

import org.apache.solr.common.SolrDocument

/**
 * Manages a SolrDocument instance and provides a fluent API for initialization.
 */
class LappsDocument {
    SolrDocument document

    public LappsDocument() {
        document = new SolrDocument()
    }

    public LappsDocument(Map map) {
        map.each { key,value ->
            document.addField(key, value)
        }
    }

    SolrDocument solr() { return document }
    LappsDocument id(String id) {
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
        add(Fields, Integer.toString(year))
    }

    LappsDocument year(String year) {
        add(Fields.YEAR, year)
    }

    LappsDocument theAbstract(String theAbstract) {
        add(Fields.ABSTRACT, theAbstract)
    }

    LappsDocument body(String body) {
        add(Fields.BODY, body)
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

    private LappsDocument add(String name, String value) {
        document.addField(name, value)
        return this
    }
}
