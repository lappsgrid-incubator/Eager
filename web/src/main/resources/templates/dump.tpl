layout "layouts/main.gsp",
title: "LAPPS/EAGER",
version: version,
content: {
    h1 "Form Data"
    p "Size: ${params.size()}"
    table(class:'grid') {
        tr {
            th "Key"
            th "Value"
        }
        params.each { entry ->
            tr {
                td entry.key
                td entry.value
            }
        }
    }
}