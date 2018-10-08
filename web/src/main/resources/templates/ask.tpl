layout 'layouts/main.gsp',
title: 'LAPPS/EAGER',
javascript: '''
$(document).ready(function() {
    $('#all').click(function() {
        $('input:checkbox').prop('checked', true);
    })
    $('#none').click(function() {
        $('input:checkbox').prop('checked', false);
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
    form(action:'ask', method:'post') {
        h1 'I am eager to help'
        fieldset {
            div {
                table {
                    tr {
                        th 'Enable'
                        th 'Algorithm'
                        th 'Weight'
                    }
                    tr {
                        td { input(type:'checkbox', name:'alg1', value:'1', checked:true) }
                        td 'Number of consecutive terms in title'
                        td { input(type:'text', name:'weight1', value:'1.0') }
                    }
                    tr {
                        td { input(type:'checkbox', name:'alg2', value:'2', checked:true) }
                        td 'Total number of search terms in title'
                        td { input(type:'text', name:'weight2', value:'1.0') }
                    }
                    tr {
                        td { input(type:'checkbox', name:'alg3', value:'3', checked:true) }
                        td 'Term position in title, earlier in the text == better score'
                        td { input(type:'text', name:'weight3', value:'1.0') }
                    }
                    tr {
                        td { input(type:'checkbox', name:'alg4', value:'4', checked:true) }
                        td 'Words in the title that are search terms'
                        td { input(type:'text', name:'weight4', value:'1.0') }
                    }
                    tr {
                        td(colspan:'3') {
                            input(type:'button', id:'all', value:'Select All')
                            input(type:'button', id:'none', value:'Clear All')
                        }
                    }
                }

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
                            input(type:'text', name: 'question', id:'question', class:'form-control input-lg', placeholder:'Ask me a question', required:'true', '')
                        }
                    }
                    tr {
                        td {
                            input(type:'submit', class:'btn btl-lg btn-primary btn-block', value:'Ask', '')
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
