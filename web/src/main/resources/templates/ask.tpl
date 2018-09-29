layout 'layouts/main.gsp',
title: 'LAPPS/EAGER',
content: {
    form(action:'ask', method:'post') {
        h1 'I am eager to help'
        fieldset {
            div(class:'column') {
                div(class:'form-group') {
                    input(type:'text', name: 'question', id:'question', class:'form-control input-lg', placeholder:'Ask me a question', required:'true', '')
                }
                div(class:'row') {
                    div(class:'col-xs-6 col-sm-6 col-md-6') {
                        input(type:'submit', class:'btn btl-lg btn-primary btn-block', value:'Ask', '')
                    }
                }
            }
        }
        div(class:'rounded-corners') {
            p "Do you want to check out the system but don't know what to ask?  Try one of these questions:"
            table(class:'grid') {
                tr {
                    td "What kinases phosphorylate AKT1 on threonine 308?"
                }
                tr {
                    td "What regulates the transcription of Myc?"
                }
                tr {
                    td "What are inhibitors of Jak1?"
                }
                tr {
                    td "What transcription factors regulate insulin expression?"
                }
                tr {
                    td "What genes does jmjd3 regulate?"
                }
                tr {
                    td "What proteins bind to the PDGF-alpha receptor in neural stem cells?"
                }
            }
        }
    }
}
