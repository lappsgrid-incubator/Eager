package org.lappsgrid.eager.mining.model

import java.util.function.Consumer

/**
 *
 */
class Section implements Iterable<Sentence> {
    String text
    List<Token> tokens
    List<Sentence> sentences

    Section() {
        tokens = []
        sentences = []
    }

    Iterator<Token> tokens() {
        return tokens.iterator()
    }

    @Override
    Iterator<Sentence> iterator() {
        return sentences.iterator()
    }

    @Override
    void forEach(Consumer<? super Sentence> action) {
        sentences.forEach(action)
    }
}
