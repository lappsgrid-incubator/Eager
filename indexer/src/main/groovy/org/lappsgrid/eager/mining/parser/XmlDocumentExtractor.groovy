package org.lappsgrid.eager.mining.parser

import org.lappsgrid.eager.mining.core.Factory
import org.lappsgrid.eager.mining.core.solr.LappsDocument

/**
 *
 */
abstract class XmlDocumentExtractor {
    XmlParser parser
    int parserId

    public XmlDocumentExtractor() {
        parser = Factory.createXmlParser()
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
