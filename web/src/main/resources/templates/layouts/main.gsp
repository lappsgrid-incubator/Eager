html {
    head {
        title(title)
        link rel:'stylesheet', type:'text/css', href:'css/main.css'
        script(src:'js/jquery.min.js', '')
        //script(src:'/js/main.js', '')
        if (include) {
            include.split(',').each {
                script(src:it, '')
            }
        }
        if (javascript) {
            script(javascript)
        }
        if (css) {
            style(css)
        }
    }
    body {
        div(class:'header') {
            h1 'The Language Applications Grid'
            h2 'Ask Me (almost) Anything'
            if (version) {
                p class:'copyright', "version $version"
            }
        }

        div(class:'content') {
            content()

            div(class:'copyright') {
                p 'Copyright 2019 The Language Applications Grid'
            }
        }
    }
}
