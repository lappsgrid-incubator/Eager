package org.lappsgrid.eager.mining.parser

import org.junit.*
import org.lappsgrid.eager.core.solr.Fields
import org.lappsgrid.eager.core.solr.LappsDocument

import static org.junit.Assert.*


/**
 *
 */
class PMCExtractorTest {

    LappsDocument document

    @Before
    void setup() {
        document = null
    }
    @After
    void teardown() {
        document = null
    }

    @Test
    void testExtractor() {
        InputStream stream = this.class.getResourceAsStream("/PMC4590010.nxml")
        assertNotNull(stream)
        XmlParser parser = new XmlParser()
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
        Node node = parser.parse(stream)

        PMCExtractor extractor = new PMCExtractor()
        document = extractor.extractValues(node)
        assertNotNull(document)
        exists(Fields.BODY)
        exists(Fields.ABSTRACT)
        validate(Fields.ID)
        validate(Fields.JOURNAL)
        validate(Fields.KEYWORDS)
        validate(Fields.PMID, "26435886")
        validate(Fields.PMC, "4590010")
        validate(Fields.DOI, "10.1080/21624054.2015.1023496")
    }

    String exists(String field) {
        String value = document.getValue(field)
        assertNotNull(value)
        assertTrue(value.length() > 0)
        return value
    }

    void validate(String field, String expected) {
        String value = exists(field)
        assertEquals(expected, value)
        println "$field: $value"
    }

    void validate(String field) {
        String value = exists(field)
        println "$field: $value"
    }
}
