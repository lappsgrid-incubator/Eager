package org.lappsgrid.eager.mining.retrieval

import org.lappsgrid.eager.mining.core.Document
import org.lappsgrid.eager.mining.core.Factory

/**
 *
 */
class DocumentLoader {
    XmlParser parser = new XmlParser()


    Document loadFromPath(String path) {
        File file = new File(path)
        if (!file.exists()) {
            return null;
        }


    }

    void test() {
        InputStream stream = this.class.getResourceAsStream("/PMC4590010.nxml")
        XmlParser parser = Factory.newXmlParser()
        Node article = parser.parse(stream)
        println article.body.text()
    }

    public static void main(String[] args) {
        new DocumentLoader().test()
    }
}
