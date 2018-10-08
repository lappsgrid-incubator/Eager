package org.lappsgrid.eager.query.elasticsearch

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.api.QueryProcessor
import org.lappsgrid.eager.query.StopWords

/**
 *
 */
class GDDSnippetQueryProcessor implements QueryProcessor {
    boolean inclusive = false
    int limit = 10

    StopWords stopwords = new StopWords()

    Query transform(String question) {
        String[] tokens = question.trim().split('\\W+')
        List<String> terms = removeStopWords(tokens)
        if (question.endsWith('?')) {
            question = question[0..-2]
        }
        String encoded = URLEncoder.encode(question, 'UTF-8')

        String query = "https://geodeepdive.org/api/snippets?limit=$limit&term=$encoded&clean"
        if (inclusive) {
            query += "&inclusive=true"
        }

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
