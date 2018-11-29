package org.lappsgrid.eager.mining.preprocess.pmc

import org.lappsgrid.eager.core.Factory
import org.lappsgrid.eager.core.solr.LappsDocument

/**
 *
 */
class Parser {
    XmlParser parser

    Parser() {
        parser = Factory.createXmlParser()
    }

    String parse(String xml) {
        return parse(parser.parseText(xml))
    }

    String parse(Node article) {
        Node front = article.front[0]
        Node meta = front.'article-meta'[0]
        String title = meta.'title-group'.'article-title'.text()
        title = normalize(title)
        String theAbstract = meta.abstract.text()
        String body = collectBody(article.body)

        StringWriter writer = new StringWriter()
        PrintWriter out = new PrintWriter(writer)
        out.println(title)
        out.println(theAbstract)
        out.println(body)
        return writer.toString()
    }

    String collectBody(NodeList nodes) {
        StringWriter writer = new StringWriter()
        PrintWriter printer = new PrintWriter(writer)
        nodes.each { node ->
            node.sec.each { section ->
                printer.println(section.title.text())
                section.p.each { paragraph ->
                    printer.println(paragraph.text())
                }
            }
        }
        return writer.toString()
    }


    String collectBody(Node node) {
        StringWriter writer = new StringWriter()
        PrintWriter printer = new PrintWriter(writer)
        node.sec.each { section ->
            printer.println(section.title.text())
            section.p.each { paragraph ->
                printer.println(paragraph.text())
            }
        }
        return writer.toString()
    }

    String collectSection(String type, NodeList nodes) {
        StringWriter writer = new StringWriter()
        PrintWriter printer = new PrintWriter(writer)
        nodes.each { node ->
            node.sec.each { section ->
                String secType = section.attribute('sec-type')
                if (secType && secType.startsWith(type)) {
                    printer.println(section.title.text())
                    section.p.each { paragraph ->
                        printer.println(paragraph.text())
                    }
                }
            }
        }
        return writer.toString()
    }

    protected String normalize(String input) {
        return input.replaceAll('\n', ' ')
                .replaceAll('\r', ' ')
                .replaceAll('\\s\\s+', ' ',)
    }

}
