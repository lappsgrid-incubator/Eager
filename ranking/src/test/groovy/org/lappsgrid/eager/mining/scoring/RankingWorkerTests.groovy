package org.lappsgrid.eager.mining.scoring

import org.junit.Test
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.ranking.CompositeRankingEngine
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

        //Need to figure out params, then run
        //RankingProcessor process = new RankingProcessor(1, params)
        //process.rank(query, params)
    }


}
