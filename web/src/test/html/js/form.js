function enable(id) {
    var btn = $(id);
    btn.css("border", "1px solid green");
    btn.css("background-color", "green");
    btn.css("cursor", "pointer");
    btn.prop("disabled", false);
}

function disable(id) {
    var btn = $(id);
    btn.css("border", "1px solid #ccc");
    btn.css("background-color", "#ccc");
    btn.css("cursor", "not-allowed");
    btn.prop("disabled", true);
}

function validateEmail(email)
{
    console.log("validating " + email)
    var re = /\S+@\S+\.\S+/;
    return re.test(email);
}

function validate(email) {
    //var email = $("#username").val();
    if (validateEmail(email.value)) {
        //enable('#submit');
        checkUser(email.value)
    }
    else {
        disable('#submit');
    }
}

function checkUser(email) {
    var url = 'http://galaxy.lappsgrid.org/api/users?key=6f716395c326f6eda8bc4cec030f307f&f_email=' + email
    var result = false
    var handler = function(response) {
        console.log(response)
        result = false
    }
    $.ajax({url:url, success: handler});
}


