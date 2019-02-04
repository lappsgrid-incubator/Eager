layout "layouts/main.gsp",
title: "LAPPS/EAGER Ratings",
version: version,
content: {
    h1 'Ratings'
    p "Size: ${data.size()}"

    div(class:'box') {
        table {
            tr {
                th 'UUID'
                th 'Score'
            }
            data.each { r ->
                tr {
                    td r.uuid
                    td r.rating
                }
            }
        }
    }
    div(class:'box') {
        a(class:'btn-ok', href:'questions', 'Questions')
        a(class:'btn-ok', href:'ask', 'Ask me another')
    }
}