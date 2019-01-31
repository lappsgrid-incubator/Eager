layout "layouts/main.gsp",
title: "LAPPS/EAGER",
version: version,
include: 'js/form.js',
javascript: '''
$(document).ready(function () {
    disable('#submit');
});


''',
content: {
    if (username) {
        div {
            h1 'You previously entered'
            table {
                tr {
                    td 'User name'
                    td username
                }
                tr {
                    td 'Dataset name'
                    td dataset
                }
            }
        }
    }

    h1 "Testing"
    form(action:'test', method:'post') {
        label(for:'username', 'User name:')
        input(id:'username', name:'username', type:'text', size:40, onkeyup:'validate(this)', '')
        label(for:'dataset', 'Dataset name: ')
        input(id:'dataset', name:'dataset', type:'text', size:40, '')
        input(type:'submit', id:'submit', class:'button', value:'Save')
    }
}