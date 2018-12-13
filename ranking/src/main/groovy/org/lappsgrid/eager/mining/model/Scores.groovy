package org.lappsgrid.eager.mining.model

import java.util.function.Consumer

/**
 *
 */
class Scores implements Iterable<Map.Entry<String,Float>> {
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

    @Override
    Iterator<Map.Entry<String,Float>> iterator() {
        return scores.iterator()
    }

    @Override
    void forEach(Consumer<? super Map.Entry> action) {
        scores.forEach(action)
    }
}
