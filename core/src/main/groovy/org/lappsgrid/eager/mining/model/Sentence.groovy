package org.lappsgrid.eager.mining.model

import java.util.function.Consumer

/**
 *
 */
class Sentence implements Iterable<Token> {
    int start
    int end

    String text
    List<Token> tokens

    Sentence() {
        tokens = []
    }

    Sentence(String text, List<Token> tokens) {
        this.text = text
        this.tokens = tokens
    }

    Iterator<Token> tokens() { return tokens.iterator() }

    @Override
    Iterator<Token> iterator() {
        return tokens.iterator()
    }

    @Override
    void forEach(Consumer<? super Token> action) {
        tokens.forEach(action)
    }
}
