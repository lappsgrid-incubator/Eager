package org.lappsgrid.eager.mining.solr.unused

/**
 *
 */
class PubmedParser {

    XmlParser parser

    public PubmedParser() {
        parser = new XmlParser();
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    }

    void process(File input, File destination) {
        if (!destination.exists()) {
            println "Output directory does not exist."
            return
        }
        println "Processing ${input.path}"
        Node set = parser.parse(input)
        set.PubmedArticle.each { article ->
            def medline = article.MedlineCitation
            String id = medline.PMID.text()
            List headings = []
            medline.MeshHeadingList.MeshHeading.each { heading ->
                headings << heading.DescriptorName.text()
            }
            println id + " " + headings.join(", ")

        }
    }

    void save(Node xml, File output) {
        FileWriter writer = new FileWriter(output)
        save(xml, writer)
    }

    void save(Node xml, Writer writer) {
        PrintWriter pwriter;
        if (writer instanceof PrintWriter) {
            pwriter = (PrintWriter) writer
        }
        else {
            pwriter = new PrintWriter(writer)
        }
        XmlNodePrinter printer = new XmlNodePrinter(pwriter)
        printer.print(xml)
    }
}
