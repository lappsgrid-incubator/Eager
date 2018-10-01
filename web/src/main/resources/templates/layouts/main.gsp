html {
    head {
        title(title)
        link rel:'stylesheet', type:'text/css', href:'css/main.css'
        script(src:'js/jquery.min.js', '')
        //script(src:'/js/main.js', '')
        if (javascript) {
            script(javascript)
        }
    }
    body {
        div(class:'header') {
            h1 'The Language Applications Grid'
            h2 'Ask Me (almost) Anything'
            /*
            nav {
                ul {
                    li {
                        a(href:'#', 'Reference')
                        ul {
                            li { a(href:'#', 'Baseline') }
                            li { a(href:'#', 'Gold') }
                        }
                    }
                    li {
                        a(href:'#', 'Type')
                        ul {
                            li { a(href:'#', 'Summary')}
                            li { a(href:'#', 'Factoid')}
                            li { a(href:'#', 'List')}
                            li { a(href:'#', 'Yes / No')}
                        }
                    }
                    li {
                        a(href:'#', 'View')
                        ul {
                            li { a(href:'/#', 'Show all' ) }
                            li { a href:'/#', 'Raw (CSV) output'}
                            li { a href:'/#', 'Evaluated' }
                            li { a href:'/#', 'Remaining' }
                        }
                    }
                    li {
                        a(href:'#', 'Admin')
                        ul {
                            li { a href:'#', 'Datasets' }
                            li { a href:'#', 'Session Info' }
                        }
                    }
                    li {
                        a(href:'#', 'Logout')
                    }
                }
            }
            div(class:'clear', '')
            */
        }

        div(class:'content') {
            content()

            div(class:'copyright') {
                p 'Copyright 2018 The Language Applications Grid'
            }
        }
    }
}
