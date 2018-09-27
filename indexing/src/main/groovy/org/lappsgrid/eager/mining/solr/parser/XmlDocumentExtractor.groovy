package org.lappsgrid.eager.mining.solr.parser

import org.lappsgrid.eager.core.solr.LappsDocument

/**
 *
 */
abstract class XmlDocumentExtractor {
    XmlParser parser
    int parserId

    public XmlDocumentExtractor() {
        parser = new XmlParser();
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    }

    abstract LappsDocument extractValues(Node root);

    String getId(String pmid, String pmc, String doi) {
        if (pmid) return pmid
        if (pmc) return pmc
        if (doi) return doi
        return UUID.randomUUID()
    }

    protected String normalize(Node node) {
        return normalize(node.text())
    }

    protected String normalize(String input) {
        return input.replaceAll('\n', ' ')
                .replaceAll('\r', ' ')
                .replaceAll('\\s\\s+', ' ',)
    }

}
