function enable(id) {
    var btn = $(id);
    btn.css("border", "1px solid green");
    btn.css("background-color", "green");
    btn.css("cursor", "pointer");
    btn.prop("disabled", false);
    console.log("enabled " + id)
}

function disable(id) {
    var btn = $(id);
    btn.css("border", "1px solid #ccc");
    btn.css("background-color", "#ccc");
    btn.css("cursor", "not-allowed");
    btn.prop("disabled", true);
    console.log("disabled " + id)
}

function validateEmail(email)
{
    var re = /\S+@\S+\.\S+/;
    return re.test(email);
}

function validate(email) {
    if (validateEmail(email.value)) {
        checkUser(email.value)
    }
    else {
        disable('#submit');
        $("#msgbox").hide()
    }
}

function checkUser(email) {
    var url = "validate?email=" + email;
    $.ajax({url:url, success: function(json) {
        var status = JSON.parse(json)
        if (status.valid) {
            enable("#submit")
            $("#msgbox").hide()
        }
        else {
            disable("#submit")
            $("#msgbox").html("<p>" + email + " is not a valid Galaxy user name.</p>")
            $("#msgbox").show()
        }
    }});
}

function rate(id, score) {
    console.log("rated " + id + " score: " + score);;
    var url = 'rate?key=' + id + '&score=' + score;
    $.ajax({url:url, success: function(rating) {
        $("#rating").text("You rated " + id + " as " + rating);
        $("#rating-display").show();
        $("#rating-buttons").hide();
    }});
}

