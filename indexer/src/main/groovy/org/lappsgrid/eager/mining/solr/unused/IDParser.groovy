package org.lappsgrid.eager.mining.solr.unused

import org.lappsgrid.eager.mining.api.Worker

import java.util.concurrent.BlockingQueue

/**
 *
 */
class IDParser extends Worker {

    XmlParser parser

    public IDParser(BlockingQueue<Object> input, BlockingQueue<Object> output) {
        this(0, input, output)
    }

    public IDParser(int n, BlockingQueue<Object> input, BlockingQueue<Object> output) {
        super("IDParser$n", input, output)
        parser = new XmlParser();
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    }

    Object work(Object object) {
        File file = (File) object
        println "$name: parsing ${file.name}"
        Values values = new Values()
        values.path = file.path
        Node article = parser.parse(file)
        Node meta = article.front.'article-meta'[0]
        values.pmid = getId(meta, 'pmid')
        values.pmc = getId(meta, 'pmc')
        values.doi = getId(meta, 'doi')
        return values
    }

    String getId(Node node, String id) {
        Node result = node.'article-id'.find { it.@'pub-id-type' == id }
        if (result == null) {
            return ""
        }
        def value = result.value()
        if (value == null) {
            return ""
        }
        return value[0].toString().trim()
    }

}
