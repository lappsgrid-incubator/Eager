layout 'layouts/main.gsp',
title: 'LAPPS/EAGER',
content: {
    h1 'The Question'
    table {
        tr {
            td 'Question'
            td data.question
        }
        tr {
            td 'Query'
            td data.query
        }
        tr {
            td 'Size'
            td data.size
        }
    }

    h1 'The Answers'
    h2 'In no particular order'

    table {
        tr {
            th 'PMID'
            th 'Year'
            th 'Title'
        }
        data.documents.each { doc ->
            tr {
                td doc.pmid
                td doc.year
                td doc.title
            }
        }
    }
    p {
        a href:'ask', 'Ask another question'
    }
}
