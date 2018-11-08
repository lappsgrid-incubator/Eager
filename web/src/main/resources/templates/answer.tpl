layout 'layouts/main.gsp',
title: 'LAPPS/EAGER',
version: version,
content: {
    h1 'The Question'
    table {
        tr {
            td 'Question'
            td data.query.question
        }
        tr {
            td 'Query'
            td data.query.query
        }
        tr {
            td 'Size'
            td data.size
        }
    }

    h1 'The Answers'
    table {
        tr {
            th 'Index'
            th 'Score'
            th 'PMID'
            th 'Year'
            th 'Title'
            if (data.keys) {
                data.keys.each { th(it) }
            }

        }
        data.documents.eachWithIndex { doc, i ->
            tr {
                td String.format("%4d", i)
                td String.format("%2.3f", doc.score)
                td { a(href:"https://www.ncbi.nlm.nih.gov/pmc/articles/${doc.pmc}/?report=classic", doc.pmc) }
                td doc.year
                td doc.title
                if (data.keys) {
                    data.keys.each { key ->
                        td String.format("%2.3f", doc.scores[key].sum())
                    }
                }
            }
        }
    }
    p {
        a href:'ask', 'Ask another question'
    }
}
