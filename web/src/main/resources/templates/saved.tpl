layout "layouts/main.gsp",
title: "LAPPS/EAGER",
version: version,
content: {
    h1 "File Transfer"
    div {
        if (error_message) {
            p(class:'alert', error_message)
        }
        p "Uploaded ${size} files to ${path}. Total bytes: ${bytes}"
        p "The files will be removed from the Galaxy server in 24 hours. Please be sure to import them into a Galaxy history before then."
        p {
            a(class:'btn-ok', href:'https://galaxy.lappsgrid.org', 'Go to LAPPS/Galaxy')
            a(class:'btn-ok', href:'ask', 'Ask Me Another')
        }
    }
}
