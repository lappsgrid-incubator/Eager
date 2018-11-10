package org.lappsgrid.eager.model

/**
 *
 */
class Scores {
    Map<String,Float> scores = [:]

    void put(String key, Float score) {
        scores[key] = score
    }

    void putAt(String key, Float score) {
        put(key, score)
    }

    Float get(String key) {
        return scores[key]
    }

    Float getAt(String key) {
        return get(key)
    }

    Float add(String key, Float value) {
        Float current = scores[key]
        if (current == null) {
            current = 0.0f
        }
        Float result = current + value
        scores[key]  = result
        return result
    }

    Float sum() {
        float total = 0f
        scores.each { key, value ->
            total += value
        }
        return total
    }
}
