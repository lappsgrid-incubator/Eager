package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.serialization.lif.Container

/**
 * Count the number of times consecutive terms appear in the title.
 */
class ConsecutiveTermEvaluator implements ScoringAlgorithm {
    @Override
    float score(Query query, Container container) {
        boolean seen = false
        int total = 0
        int count = 0
        String[] tokens = tokenize(input)
        tokens.each { word ->
            if (query.terms.contains(word)) {
                if (seen) {
                    if (count == 0) {
                        // This is the second consecutive occurence, so count the first.
                        count = 1
                    }
                    ++count
                }
                seen = true
            }
            else {
                total += count
                count = 0
                seen = false
            }
        }
        total += count
//        println "Count: $total"
//        println "Length: ${tokens.length}"
        return ((float) total) / tokens.length
    }

    @Override
    String name() {
        return 'ConsecutiveTermEvaluator'
    }
    @Override
    String abbrev() {
        return "ngrams"
    }
}
