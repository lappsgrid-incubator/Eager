package org.lappsgrid.eager.rank

/**
 *
 */
trait Tokenizer {
    String[] tokenize(String string) {
        return string.trim().split("\\s+")
    }
}