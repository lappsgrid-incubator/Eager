package org.lappsgrid.eager.mining.scoring

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section

/**
 * Count the number of times consecutive terms appear in the title.
 */
class ConsecutiveTermEvaluator extends AbstractScoringAlgorithm {
    @Override
    float score(Query query, Section section) {
        boolean seen = false
        int total = 0
        int count = 0
        int n = 0
        section.tokens.each { word ->
            ++n
            if (contains(query.terms, word)) {
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
        if (n == 0) {
            return 0f
        }
        total += count
//        println "Count: $total"
//        println "Length: ${tokens.length}"
        return ((float) total) / n
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
