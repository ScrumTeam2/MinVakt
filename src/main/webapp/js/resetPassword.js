/**
 * Created by evend on 1/25/2017.
 */
$(document).ready(function () {
    $("#resetpass").click(function (e) {
        e.preventDefault();
        var emptyField = false;
        var $username = $("#username");

        if(!$username.val()) {
            $username.addClass("error");
            emptyField = true;
        }

        if(!emptyField){
            var data = {"email" : $username.val()};
            $.ajax({
                url: "../rest/user/forgottenpass",
                type: 'POST',
                data: data,
                success: passwordResetSuccess,
                error: passwordResetFailure,
            });
        }
    })
});
function passwordResetSuccess(data) {
    $("#popup-title").text("Suksess");
    $("#popup-message").text("Nytt passord er blitt sent til din mail. Trykk for å gå til login.");
    var $btn1 = $("#popup-btn1");
    var $btn2 = $("#popup-btn2");
    $btn1.text("Gå til login");
    $btn2.text("Avbryt");
    $(".popup").show();
    $btn2.click(function () {
        $(".popup").hide();
    });
    $btn1.click(function () {
        window.location = "login.html";
    });
}
function passwordResetFailure(data) {
    //417 = Expectation failed
    if(data.status == 417) {
        $("#popup-title").text("En feil har oppstått");
        $("#popup-message").text("Brukernavnet er riktig, men vi fant ingen mail å sende til. Prøv på nytt med mailen din.");
        $("#popup-btn2").hide();
        var $btn1 = $("#popup-btn1");
        $btn1.text("Ok");
        $(".popup").show();
        $btn1.click(function () {
            $(".popup").hide();
        });
    }
    else {
        $("#popup-title").text("En feil har oppstått");
        $("#popup-message").text("Vi kunne ikke finne en bruker med den mailen. Prøv på nytt med mailen din.");
        $("#popup-btn2").hide();
        $("#popup-btn1").text("Ok");
        $(".popup").show();
        $("#popup-btn1").click(function () {
            $(".popup").hide();
        })
    }
}