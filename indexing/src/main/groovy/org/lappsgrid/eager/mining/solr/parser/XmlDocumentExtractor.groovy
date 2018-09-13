package org.lappsgrid.eager.mining.solr.parser

import org.apache.solr.common.SolrDocument

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

    abstract SolrDocument extractValues(File file);

    protected String normalize(String input) {
        return input.replaceAll('\n', ' ')
                .replaceAll('\r', ' ')
                .replaceAll('\\s\\s+', ' ',)
    }

}
