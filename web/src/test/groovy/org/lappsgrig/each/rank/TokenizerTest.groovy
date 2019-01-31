package org.lappsgrig.each.rank

import org.junit.Test
//import org.lappsgrid.eager.rank.Tokenizer

/**
 *
 */
class TokenizerTest {

    @Test
    void textWithApostrophe() {
        String input = "Earth's dark age?"
        TestTokenizer tokenizer = new TestTokenizer()
        List tokens = tokenizer.tokenize(input)
        println tokens.size()
        tokens.each { println it }
    }
}

class TestTokenizer {
    List<String> tokenize(String input) {
        List<String> result = []
        StreamTokenizer tokenizer = new StreamTokenizer(new StringReader(input))
        while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
            result.add(tokenizer.sval)
        }
        return result
    }
}