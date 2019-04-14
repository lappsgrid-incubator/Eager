package org.lappsgrid.eager.mining.scoring

import org.junit.Test
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.ranking.CompositeRankingEngine
import org.lappsgrid.eager.mining.ranking.RankingProcessor
import org.lappsgrid.eager.mining.ranking.model.Document

class RankingWorkerTests extends TestBase{

    ScoringAlgorithm create() {
        return new ConsecutiveTermEvaluator()
    }

    @Test
    void SimpleTest(){
        Document document1 = new Document()
        document1.title = makeSection("This is a test title.")
        document1.articleAbstract = makeSection("This is a test abstract. Hopefully it returns a score.")

        Document document2 = new Document()
        document2.title = makeSection("a b c d e f")
        document2.articleAbstract = makeSection("g h i j k l m n o p q r s t u v w x y z.")

        Query query = makeQuery("test it score")

        List<Document> test = new ArrayList<>()
        test.add(document1)
        test.add(document2)

        Map<String, Float> params = [
                'abstract-checkbox-1': 1.0f,
                'abstract-checkbox-2': 1.0f,
                'abstract-checkbox-3': 1.0f,
                'abstract-checkbox-4': 1.0f,
                'abstract-checkbox-5': 1.0f,
                'abstract-checkbox-6': 1.0f,
                'abstract-checkbox-7': 1.0f,
                'abstract-weight-' : 1.0f,
                'title-checkbox-1': 1.0f,
                'title-checkbox-2': 1.0f,
                'title-checkbox-3': 1.0f,
                'title-checkbox-4': 1.0f,
                'title-checkbox-5': 1.0f,
                'title-checkbox-6': 1.0f,
                'title-checkbox-7': 1.0f,
                'title-weight-': 1.0f
        ]

        RankingProcessor process = new RankingProcessor(1, params)
        List<Document> resultNew = process.rank(query, test)
        CompositeRankingEngine ranker = new CompositeRankingEngine(params)
        List<Document> resultOld =  ranker.rank(query, test)

        assert(resultNew[0].getScore() == resultOld[0].getScore())


    }


}
