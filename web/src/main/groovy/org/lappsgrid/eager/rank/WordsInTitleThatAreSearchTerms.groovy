package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 * How many words in the title are terms in the question.
 */
class WordsInTitleThatAreSearchTerms implements ScoringAlgorithm, Tokenizer {
    @Override
    float score(Query query, Document document) {
        int count = 0
        tokenize(document.title).each { token ->
            if (query.terms.contains(token)) {
                ++count
            }
        }
        return ((float)count) / document.title.length()
    }

    @Override
    String name() {
        return 'TermFrequencyEvaluator'
    }
}
