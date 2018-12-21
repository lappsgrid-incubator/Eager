package org.lappsgrid.eager.nlp

import java.util.function.Consumer

/**
 *
 */
class Sentence implements Iterable<Token> {
    String sentence
    List<Token> tokens

    Sentence() {
        tokens = []
    }

    Sentence(String sentence, List<Token> tokens) {
        this.sentence = sentence
        this.tokens = tokens
    }

    @Override
    Iterator<Token> iterator() {
        return tokens.iterator()
    }

    @Override
    void forEach(Consumer<? super Token> action) {
        tokens.forEach(action)
    }
}
