package org.lappsgrid.eager.mining.parser

import org.lappsgrid.eager.core.solr.LappsDocument

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

    LappsDocument extractValues(Node pubmed) {
        Node medline = pubmed.MedlineCitation[0]
        def article = medline.Article[0]
        String title = article.ArticleTitle.text()
        String articleAbstract = article.Abstract.AbstractText.text()
        def journal = article.Journal
        //String journal = article.Journal.Title.text()
        String year = journal.JournalIssue.PubDate.Year.text()
        String pmid = medline.PMID.text()
        //PubmedArticle.PubmedData.ArticleIdList.ArticleId[@IdType = 'pmc'
        def data = pubmed.PubmedData
        def pmc = data.ArticleIdList.ArticleId.find { it.@IdType == 'pmc' }

        List<String> mesh = []
        medline.MeshHeadingList.MeshHeading.each { Node heading ->
            mesh.add(heading.DescriptorName.text())
        }

        LappsDocument document = new LappsDocument()
                .title(title)
                .theAbstract(articleAbstract)
                .journal(journal.Title.text())
                .year(year)
                .pmid(pmid)
                .pmc(pmc.text())
                .mesh(mesh.join(" "))
                .pubmed()

        return document
    }
}
