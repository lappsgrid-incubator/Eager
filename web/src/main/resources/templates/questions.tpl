layout "layouts/main.gsp",
title: "LAPPS/EAGER Questions",
version: version,
content: {
    h1 'Questions'
    div(class:'box') {
        table {
            tr {
                th 'UUID'
                th 'Question'
            }
            data.each { q ->
                tr {
                    td q.uuid
                    td q.text
                }
            }
        }
    }
    div(class:'box') {
        a(class:'btn-ok', href:'ratings', 'Ratings')
        a(class:'btn-ok', href:'ask', 'Ask me another')
    }
}