package org.lappsgrid.eager.query

import org.junit.Test

/**
 *
 */
class StopWordsTest {

    @Test
    void defaultConstuctor() {
        new StopWords()
    }

    @Test
    void stringParamConstructor() {
        new StopWords("/stopwords.txt")
    }

    @Test
    void size() {
        StopWords words = new StopWords()
        assert 174 == words.words.size()
        println "StopWordsTest.size"
    }

    @Test
    void lookup() {
        // Search for a few common stop words
        StopWords words = new StopWords()
    }
}
