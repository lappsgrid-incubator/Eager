package org.lappsgrid.eager.mining.solr.parser

import org.junit.*
import org.lappgrid.eager.core.solr.Fields
import org.lappgrid.eager.core.solr.LappsDocument
import static org.junit.Assert.*

/**
 *
 */

class PubmedExtractorTest {

    LappsDocument document

    @Test
    public void extractor() {
        InputStream stream = this.class.getResourceAsStream("/1-00000987.xml")
        assertNotNull(stream)
        XmlParser parser = new XmlParser()
        Node root = parser.parse(stream)

        PubmedExtractor extractor = new PubmedExtractor()
        document = extractor.extractValues(root)

        assertEquals("PMC1428951", document.getValue(Fields.PMC))
        assertEquals("9967", document.getValue(Fields.PMID))
        assertEquals(LappsDocument.Type.PUBMED, document.getValue(Fields.TYPE))
        String title = document.getValue(Fields.TITLE)
        print(Fields.TITLE)
        print(Fields.JOURNAL)
        print(Fields.MESH)
        print(Fields.ABSTRACT)
        assertEquals("1976", document.getValue(Fields.YEAR))
    }

    void print(String field) {
        String value = document.getValue(field)
        assertNotNull(value)
        assertTrue(value.length() > 0)
        println "$field: $value"
    }
}
