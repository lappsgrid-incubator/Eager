package org.lappsgrid.eager.mining.solr.parser

import org.apache.solr.common.SolrDocument
import org.apache.solr.common.SolrInputDocument
import org.lappgrid.eager.core.solr.LappsDocument

/**
 *
 */
public class PMCExtractor extends XmlDocumentExtractor
{
    public PMCExtractor()
    {
    }

    public LappsDocument extractValues(Node article) {
        /*
        private final String exprXpathAbstract = "//abstract";
        private final String exprXpathBody = "//body";
        private final String exprXpathTitle = "//journal-meta/journal-title-group/journal-title";
        private final String exprXpathPmid = "//article-id[@pub-id-type='pmid']";
        private final String exprXpathPmc = "//article-id[@pub-id-type='pmc']";
        private final String exprXpathArticleTitle = "//article-title";

        private final String exprXpathAuthor = "//contrib[@contrib-type='author']/name";
        private final String exprXpathKeywords = "//kwd-group/kwd";
        private final String exprXpathSectionTitle = "//sec/title";
        private final String exprXpathRef = "//ref/mixed-citation|//ref/element-citation";
        private final String exprXpathYearP = "//pub-date[@pub-type='ppub']/year";
        private final String exprXpathYearE = "//pub-date[@pub-type='epub']/year";
        private final String exprXpathFigCaption = "//floats-group";
        */
//        println "PMCExtractor $parserId: parsing ${file.name}"
//        Node article = parser.parse(file)
        Node front = article.front[0]
        Node meta = front.'article-meta'[0]
        String journal = front.'journal-meta'.'journal-title-group'.'journal-title'
        String pmid = getIdValue(meta, 'pmid')
        String pmc = getIdValue(meta, 'pmc')
        String doi = getIdValue(meta, 'doi')
        String title = meta.'title-group'.'article-title'.text()
        String year = '0'
        Node pubDate = meta.'pub-date'.find { it.@'pub-type' == 'ppub' }
        if (pubDate == null) {
            pubDate = meta.'pub-date'.find { it.@'pub-type' == 'epub' }
        }
        if (pubDate != null) {
            year = pubDate.year.text()
        }
        title = normalize(title)

        List keywords = []
        meta.'kwd-group'.kwd.each { kwd ->
            keywords << normalize(kwd)
        }

        LappsDocument document = new LappsDocument()
            .id(getId(pmid, pmc, doi))
            .pmid(pmid)
            .pmc(pmc)
            .doi(doi)
            .journal(journal)
            .title(title)
            .year(year)
            .keywords(keywords)
            .body(article.body.text())
            .theAbstract(meta.abstract.text())
//        SolrInputDocument document = new SolrInputDocument();
//        document.addField("pmid", pmid);
//        document.addField("pmc", pmc);
//        document.addField("doi", doi)
//        document.addField("journal", journal)
//        document.addField("title", title)
//        document.addField("year", year as int)
//        document.addField("keywords", keywords)
//        document.addField("body", article.body.text())
//        document.addField("abstract", meta.abstract.text())
        return document
    }

    String getIdValue(Node node, String id) {
        Node result = node.'article-id'.find { it.@'pub-id-type' == id }
        if (result == null) {
            return ""
        }
        def value = result.value()
        if (value == null) {
            return ""
        }
        return value[0]
    }

}
