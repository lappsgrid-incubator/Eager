package org.lappsgrid.eager.mining.scoring

/**
 *
 */
trait Tokenizer {
    String[] tokenize(String string) {
        return string.trim().split("\\s+")
    }
}