layout 'layouts/main.gsp',
title: 'LAPPS/EAGER',
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
            th 'Score'
            th 'PMID'
            th 'Year'
            th 'Title'
            if (data.keys) {
                data.keys.each { th(it) }
            }

        }
        data.documents.each { doc ->
            tr {
                td String.format("%2.3f", doc.score)
                td { a(href:"show?path=${doc.path}", doc.pmid) }
                td doc.year
                td doc.title
                if (data.keys) {
                    data.keys.each { key ->
                        td String.format("%2.3f", doc.scores[key])
                    }
                }
            }
        }
    }
    p {
        a href:'ask', 'Ask another question'
    }
}
