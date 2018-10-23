package org.lappsgrid.eager.rank

import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.model.Document

/**
 *  Scores documents by combining several weighted RankingEngines.
 */
class CompositeRankingEngine {

    Map<String,RankingEngine> engines = [:]

    Map<String,Closure> fieldExtractors = [
            title: { it.title },
            abstract: { it.articleAbstract },
            intro: { it.intro }
    ]

    CompositeRankingEngine(Map params) {
        params.each { String key, String value ->
            Triple triple = new Triple(key)
            if (triple.control == 'checkbox') {
                ScoringAlgorithm algorithm = AlgorithmRegistry.get(value)
                if (algorithm != null) {
                    RankingEngine engine = engines[triple.section]
                    if (engine == null) {
                        engine = new RankingEngine()
                        engine.field = fieldExtractors[triple.section]
                        engines.put(triple.section, engine)
                    }
                    String weightKey = key.replace("checkbox", "weight")
                    String weight = params.get(weightKey)
                    if (weight) {
                       engine.add(new WeightedAlgorithm(algorithm, weight as float))
                    }
                    else {
                        // TODO Log this as a programming error in the form.
                    }
                }
                else {
                    // TODO Log this as a programming error in the form.
                }
            }
            else if (triple.id == 'x') {
                RankingEngine engine = engines[triple.section]
                if (engine) {
                    engine.weight = value as float
                }
                else {
                    // TODO Log this as a programming error in the form.
                }
            }

        }
    }

    List<Document> rank(Query query, List<Document> documents) {
        engines.each { String key, RankingEngine engine ->
            engine.rank(query, documents)
        }
        return documents.sort { a,b -> a.score <=> b.score }
    }

    class Triple {
        String section
        String control
        String id

        Triple(String input) {
            String[] parts = input.tokenize("-")
            if (parts.length != 3) {
                section = control = id = 'unknown'
                return
            }
            section = parts[0]
            control = parts[1]
            id = parts[2]
        }


    }
}
