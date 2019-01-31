layout 'layouts/main.gsp',
title: 'LAPPS/EAGER',
version: version,
javascript: '''
$(document).ready(function() {
    $('#all-title').click(function() {
        $('.title-box').prop('checked', true);
    })
    $('#none-title').click(function() {
        $('.title-box').prop('checked', false);
    })
    $('#all-abs').click(function() {
        $('.abs-box').prop('checked', true);
    })
    $('#none-abs').click(function() {
        $('.abs-box').prop('checked', false);
    })
    $('#domain').click(function() {
        console.log("Domain button clicked.")
        console.log("Value: " + $('#domain').val())
        if ($('#domain').val() == 'bio') {
            console.log("Showing bio questions.")
            $('#bio-questions').show()
            $('#geo-questions').hide()
        }
        else {
            console.log("Showing geo questions.")
            $('#bio-questions').hide()
            $('#geo-questions').show()
        }
    })
})
''',
content: {
    form(action:'question', method:'post', class:'no-border') {
        h1 'I am eager to help'
        fieldset(class:'no-border') {
            div(class:"column") {
                h3 "Title"
                table {
                    tr {
                        th 'Enable'
                        th 'Algorithm'
                        th 'Weight'
                    }
                    descriptions.eachWithIndex { desc, i ->
                        tr {
                            td { input(type:"checkbox", name:"title-checkbox-${i+1}", class:"title-box", value:(i+1), checked:true) }
                            td desc
                            td { input(type:'text', name:"title-weight-${i+1}", value:"1.0") }
                        }
                    }
                    tr {
                        td(colspan:'3') {
                            input(type:'button', id:'all-title', value:'Select All')
                            input(type:'button', id:'none-title', value:'Clear All')
                        }
                    }
                    tr {
                        td(colspan:'2') { label(for:'title-weight', 'Weight') }
                        td {
                            input(id:'title-weight', type:'text', name:'title-weight-x', value:'0.9')
                        }
                    }
                }
            }
            div(class:"column") {
                h3 "Abstract"
                table {
                    tr {
                        th 'Enable'
                        th 'Algorithm'
                        th 'Weight'
                    }
                    descriptions.eachWithIndex { desc, i ->
                        tr {
                            td { input(type:"checkbox", name:"abstract-checkbox-${i+1}", class:"abs-box", value:(i+1), checked:true) }
                            td desc
                            td { input(type:'text', name:"abstract-weight-${i+1}", value:"1.0") }
                        }
                    }
                    tr {
                        td(colspan:'3') {
                            input(type:'button', id:'all-abs', value:'Select All')
                            input(type:'button', id:'none-abs', value:'Clear All')
                        }
                    }
                    tr {
                        td(colspan:'2') { label(for:'abstract-weight', 'Weight') }
                        td {
                            input(id:'abstract-weight', type:'text', name:'abstract-weight-x', value:'1.1')
                        }
                    }
                }
            }
            div(class:'clear') {
                br()
                br()
                table {
                    tr {
                        td {
                            label(for:'domain', 'Domain')
                            select(id:'domain', name:'domain') {
                                option(id:'bio', value:'bio', 'Biomedical')
                                option(id:'geo', value:'geo', 'Geoscience')
                            }
                        }
                    }
                    tr {
                        td(colspan:'2') {
                            input(type:'text', name: 'question', id:'question', placeholder:'Ask me a question.', required:'true', '')
                        }
                    }
                    tr {
                        td {
                            input(type:'submit', class:'btn-ok', value:'Ask', '')
                        }
                    }
                }
                /*
                div(class:'form-group') {
                    input(type:'text', name: 'question', id:'question', class:'form-control input-lg', placeholder:'Ask me a question', required:'true', '')
                }
                div(class:'row') {
                    div(class:'col-xs-6 col-sm-6 col-md-6') {
                        input(type:'submit', class:'btn btl-lg btn-primary btn-block', value:'Ask', '')
                    }
                }
                */
            }
        }
        div(class:'rounded-corners') {
            p "Do you want to check out the system but don't know what to ask?  Try one of these questions:"
            table(id:'bio-questions', class:'grid') {
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
            table(id:'geo-questions', class:'grid hidden') {
                tr {
                    td "What happened during the Earth's Dark Age?"
                }
                tr {
                    td "Why does Earth have plate tectonics and continents?"
                }
                tr {
                    td "How are Earth processes controlled by material properties?"
                }
                tr {
                    td "How do fluid flow and transport affect the human environment?"
                }
            }
        }
    }
}
