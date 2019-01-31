package org.lappsgrid.eager.mining.scoring

import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.core.json.Serializer
import org.lappsgrid.eager.mining.model.Token
import org.lappsgrid.eager.mining.ranking.model.Document

/**
 *
 */
@Ignore
class AlgorithmTest {

    Query query
    DocumentList docs

    @Before
    void setup() {
        InputStream stream = this.class.getResourceAsStream('/query.json')
        assert stream != null
        query = Serializer.parse(stream.text, Query)
        println "Query is ${query.question}"
        stream = this.class.getResourceAsStream("/files.json")
        docs = Serializer.parse(stream.text, DocumentList)
        Closure lower_case = { Token token ->
            token.word = token.word.toLowerCase()
            token.lemma = token.lemma.toLowerCase()
        }
        docs.each { Document doc ->
            doc.title.tokens().each { lower_case it }
            doc.articleAbstract.tokens().each { lower_case it }
        }
        println "Parsed ${docs.size()} documents"
    }

    @After
    void teardown() {
        query = null
        docs = null
    }

    @Test
    void termFrequency() {

        Document doc = docs[1]
        ScoringAlgorithm algorithm = new TermFrequencyEvaluator()
        float score = algorithm.score(query, doc.title)
        println "Text: " + doc.title.text
        println String.format("Score: %2.3f", score)
    }

    @Test
    void termPosition() {

        Document doc = docs[3]
        ScoringAlgorithm algorithm = new TermPositionEvaluator()
        float score = algorithm.score(query, doc.title)
        println "Text: " + doc.title.text
        println String.format("Score: %2.3f", score)
    }

    @Test
    void consecutiveTerms() {
        Document doc = docs[3]
        Query q = new Query()
        q.question = 'constitutively active prevents mammary epithelial'
        q.query = 'constitutively active prevents mammary epithelial'
        q.terms = 'constitutively active prevents mammary epithelial'.tokenize()
        ScoringAlgorithm algorithm = new ConsecutiveTermEvaluator()
        float score = algorithm.score(q, doc.title)
        println "Text: " + doc.title.text
        println String.format("Score: %2.3f", score)
    }

    @Test
    void percentageTerm() {
        Document doc = docs[3]
        Query q = new Query()
        q.question = 'constitutively active prevents mammary epithelial'
        q.query = 'constitutively active prevents mammary epithelial'
        q.terms = 'constitutively active prevents mammary epithelial'.tokenize()
        ScoringAlgorithm algorithm = new PercentageOfTermsEvaluator()
        float score = algorithm.score(q, doc.title)
        println "Text: " + doc.title.text
        println String.format("Score: %2.3f", score)
    }
}

class DocumentList extends ArrayList<Document> { }
