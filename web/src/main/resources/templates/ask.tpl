layout 'layouts/main.gsp',
title: 'LAPPS/EAGER',
content: {
    form(action:'/ask', method:'post') {
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
    }
}
