package org.lappsgrid.eager.mining.solr.parser

import org.apache.solr.common.SolrDocument

/**
 *
 */
class PubmedExtractor extends XmlDocumentExtractor {

    XmlParser parser

    public PubmedExtractor() {
        parser = new XmlParser()
        parser.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false)
        parser.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false)
    }
    /*
    private final String exprXpathMedline = "//MedlineCitationSet/MedlineCitation";
    private final String exprXpathPubMed = "//PubmedArticleSet/PubmedArticle";
    private final String exprXpathAbstract = "//Abstract";
    private final String exprXpathTitle = "//Journal/Title";
    private final String exprXpathPmid = "//PMID";
    private final String exprXpathPmc = "//ArticleId[@IdType='pmc']";
    private final String exprXpathArticleTitle = "//ArticleTitle";
    private final String exprXpathAuthor = "//Author[@ValidYN='Y']";
    private final String exprXpathMesh = "//MeshHeadingList/MeshHeading";
     */

    SolrDocument extractValues(File file) {
        Node pubmed = parser.parse(file)
        Node medline = pubmed.MedlineCitation
        Node article = medline.Article
        String title = article.Title.text()
        String articleAbstract = article.Abstract.text()
        String journal = article.Journal.Title.text()
        String pmid = medline.PMID.text()
        //PubmedArticle.PubmedData.ArticleIdList.ArticleId[@IdType = 'pmc'
        Node pmc = pubmed.PubmedData.ArticleIdList.ArticleId.find { it.@IdType == 'pmc' }


    }
}
