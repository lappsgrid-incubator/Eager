package org.lappsgrid.eager.mining.web.nlp

import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrDocumentList
import org.elasticsearch.search.suggest.term.TermSuggestion
import org.lappsgrid.eager.mining.api.Query
import org.lappsgrid.eager.mining.model.Section
import org.lappsgrid.eager.mining.model.Sentence
import org.lappsgrid.eager.mining.model.Token
import org.lappsgrid.eager.mining.ranking.CompositeRankingEngine
import org.lappsgrid.eager.mining.ranking.RankingProcessor
import org.lappsgrid.eager.mining.ranking.model.Document

/***
 * This is a testing file so I can figure out how DocumentProcessor works and if the new RankingProcessor work
 */
class GenerateProcessed {

    static Section makeSection(String input) {
        Section section = new Section()
        section.text = input
        Sentence sentence = new Sentence()
        sentence.text = input
        List<String> words = input.tokenize(" ")
        words.each { String word ->
            Token token = new Token()
            token.word = word
            token.lemma = word
            token.pos = 'NN'
            token.category = 'category'
            sentence.tokens.add(token)
            section.tokens.add(token)
        }
        section.sentences.add(sentence)
        return section
    }

    public static void test(){
        Document d1 =  new Document()
        Document d2 = new Document()

        d1.title = makeSection("TSMYD3-mediated lysine methylation in the PH domain is critical for activation of AKT1")
        d1.articleAbstract = makeSection("AKT1 is a cytosolic serine/threonine kinase that is overexpressed in various types of cancer and has a central role in human tumorigenesis. Although it is known that AKT1 is post-translationally modified in various ways including phosphorylation and ubiquitination, methylation has not been reported so far. Here we demonstrate that the protein lysine methyltransferase SMYD3 methylates lysine 14 in the PH domain of AKT1 both in vitro and in vivo. Lysine 14-substituted AKT1 shows significantly lower levels of phosphorylation at threonine 308 than wild-type AKT1, and knockdown of SMYD3 as well as treatment with a SMYD3 inhibitor significantly attenuates this phosphorylation in cancer cells. Furthermore, substitution of lysine 14 diminishes the plasma membrane accumulation of AKT1, and cancer cells overexpressing lysine 14-substiuted AKT1 shows lower growth rate than those overexpressing wild-type AKT1. These results imply that SMYD3-mediated methylation of AKT1 at lysine 14 is essential for AKT1 activation and that SMYD3-mediated AKT1 methylation appears to be a good target for development of anti-cancer therapy.")
        d2.title = makeSection("Increased level of phosphorylated akt measured by chemiluminescence-linked immunosorbent assay is a predictor of poor prognosis in primary breast cancer overexpressing ErbB-2")
        d2.articleAbstract = makeSection("Akt1, Akt2 and Akt3 kinases are downstream components of phosphoinositol 3-kinase derived signals from receptor tyrosine kinases, which influence cell growth, proliferation and survival. Akt2 overexpression and amplification have been described in breast, ovarian and pancreatic cancers. The present study was designed to investigate the prognostic significance of activated Akt in primary breast cancer and its association with other tumour biomarkers.")
        List<Document> processed = new ArrayList()
        processed.add(d1)
        processed.add(d2)


        Query query = new Query()
        query.setQuestion("kinases phosphorylate AKT1 threonine 308")
        query.setQuery("kinases phosphorylate AKT1 threonine 308")
        query.setTerms("kinases phosphorylate AKT1 threonine 308".tokenize())


        Map<String, String> params = [
                'abstract-checkbox-1' : '1.0',
                'abstract-checkbox-2': '1.0',
                'abstract-checkbox-3': '1.0',
                'abstract-checkbox-4': '1.0',
                'abstract-checkbox-5': '1.0',
                'abstract-checkbox-6': '1.0',
                'abstract-checkbox-7': '1.0',
                'abstract-weight-x' : '1.0',
                'title-checkbox-1': '1.0',
                'title-checkbox-2': '1.0',
                'title-checkbox-3': '1.0',
                'title-checkbox-4': '1.0',
                'title-checkbox-5': '1.0',
                'title-checkbox-6': '1.0',
                'title-checkbox-7': '1.0',
                'title-weight-x': '1.0'
        ]

        RankingProcessor process = new RankingProcessor(params)
        List<Document> resultNew = process.rank(query, processed)

        CompositeRankingEngine ranker = new CompositeRankingEngine(params)
        List<Document> resultOld =  ranker.rankOld(query, processed)


        println(resultOld[0].getScore())
        println(resultOld[1].getScore())
        println(resultNew[0].getScore())
        println(resultNew[1].getScore())
    }



    public static void main(String[] args) {
        test()


    }



}
