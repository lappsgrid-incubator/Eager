layout 'layouts/main.gsp',
title: 'LAPPS/EAGER',
content: {
    div {
        h1 'Algorithm Settings'
        table {
            tr {
                th 'Name'
                th 'Value'
            }
            params.each {  e ->
                tr {
                    td e.key
                    td e.value
                }
            }
        }
    }
}
