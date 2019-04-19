layout 'layouts/main.gsp',
title: 'LAPPS/EAGER',
version: version,
include: 'js/form.js',
javascript: '''
$(document).ready(function(){
    disable("#submit");
});
''',
content: {
    h1 'The Question'
    table {
        tr {
            td 'Question'
            td data.query.question
        }
        tr {
            td 'Query'
            td data.query.query
        }
        tr {
            td 'Size'
            td data.size
        }
        tr {
            td 'Time'
            td duration
        }
    }
    h1 "Send Results To Galaxy"
    div(id:'msgbox', class:'alert hidden', '')
    form(action:'save', method:'post', class:'box') {
        yieldUnescaped '''<p>To send json to <a href="https://galaxy.lappsgrid.org">LAPPS/Galaxy</a>
            you must be a registered user.  Enter you Galaxy username (email address) below and the files will be available
            in the <i>Upload file</i> dialog (click the <i>Choose FTP files</i> button). If files with the same name already exists on the Galaxy server
            they will be overwritten.</p>'''
        fieldset(class:'no-border') {
            input(type:'email', id:'username', name:'username', required:'true', placeHolder:'Enter your Galaxy username', oninput:'validate(this)', size:40)
            input(type:'text', name:'key', id:'key', style:'display:none', value:key)
            input(type:'submit', id:'submit', class:'submit', value:'Send to Galaxy')
        }
    }
    h3 "Rate These Answers"
    div id:'rating-buttons', class:'box', {
        input id:'rate-good', type:'button', class:'btn-ok', value:'Good', onclick:"rate('$key', 1)", ''
        input id:'rate-meh', type:'button', class:'btn-warn', value:'Meh', onclick:"rate('$key', 0)", ''
        input id:'rate-bad', type:'button', class:'btn-error', value:'Bad', onclick:"rate('$key', -1)", ''
    }
    div id:'rating-display', class:'box hidden', {
        p id:'rating', 'Not yet rated.'
    }

    h1 'The Answers'
    table(class:'answers grid') {
        tr {
            th 'Index'
            th 'Score'
            th 'PMID'
            th 'Year'
            th 'Title'
            if (data.keys) {
                data.keys.each { key ->
                    data.documents[0].scores[key].each { e ->
                        th(e.key)
                    }
                    th(key)
                }
            }

        }
        data.documents.eachWithIndex { doc, i ->
            tr {
                td String.format("%4d", i)
                td String.format("%2.3f", doc.score)
                td { a(href:"https://www.ncbi.nlm.nih.gov/pmc/articles/${doc.pmc}/?report=classic", target:'_blank', doc.pmc) }
                td doc.year
                td doc.title.text
                if (data.keys) {
                    data.keys.each { key ->
                        doc.scores[key].each { e ->
                            td String.format("%2.3f", e.value)
                        }
                        td class:'sum', String.format("%2.3f", doc.scores[key].sum())
                    }
                }
            }
        }
    }
    div(class:'section') {
        p {
            a href:'ask', class:'plain', 'Ask another question'
        }
    }
}
