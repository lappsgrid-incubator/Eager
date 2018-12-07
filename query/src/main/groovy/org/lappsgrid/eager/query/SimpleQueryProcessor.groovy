package org.lappsgrid.eager.query

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.api.QueryProcessor

/**
 * Removes stop words and creates a conjunction of the remaining words.
 */
class SimpleQueryProcessor implements QueryProcessor {

    StopWords stopwords = new StopWords()

    Query transform(String question) {
        String[] tokens = question.trim().toLowerCase().split('\\W+')
        List<String> terms = removeStopWords(tokens)
        String query = terms.collect { 'body:' + it }.join(' AND ')

        return new Query()
                .query(query)
                .question(question)
                .terms(terms);
    }

    List<String> removeStopWords(String[] tokens) {
        Closure filter = { List list, String word ->
            if (!stopwords.contains(word)) {
                list.add(word)
            }
            return list
        }
        return tokens.inject([], filter)
    }

}
