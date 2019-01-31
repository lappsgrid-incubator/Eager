layout "layouts/main.gsp",
title: "LAPPS/EAGER",
version: version,
content: {
    h1 "File Transfer"
    div {
        if (error_message) {
            p(class:'alert', error_message)
        }
        p "Wrote ${size} files to ${path}. Total bytes: ${bytes}"
    }
}
