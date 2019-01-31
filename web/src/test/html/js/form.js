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
        enable('#submit');
    }
    else {
        disable('#submit');
    }
}


