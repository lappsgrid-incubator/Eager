layout 'layouts/main.gsp',
title: 'LAPPS/EAGER',
content: {
    h1 'The Question'
    table {
        tr {
            td 'Question'
            td json.query.question
        }
        tr {
            td 'Query'
            td json.query.query
        }
        tr {
            td 'Size'
            td json.size
        }
    }

    h1 'The Answers'
    table {
        tr {
            th 'Score'
            th 'DOI'
            th 'Year'
            th 'Title'
            if (json.keys) {
                json.keys.each { th(it) }
            }

        }
        json.documents.each { doc ->
            tr {
                td String.format("%2.3f", doc.score)
                td doc.doi
                td doc.year
                td doc.title
                if (json.keys) {
                    json.keys.each { key ->
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
