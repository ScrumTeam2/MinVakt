$(document).ready(function () {
    $("#resetpass").click(function (e) {
        e.preventDefault();
        var emptyField = false;

        var $oldPass = $("#oldPass");
        var $newPass = $("#newPass");
        var $reNewPass = $("#reNewPass");

        if(!$oldPass.val()){
            $oldPass.addClass("error");
            emptyField = true;
        }
        if(!$newPass.val()){
            $newPass.addClass("error");
            emptyField = true;
        }
        if(!$reNewPass.val()){
            $reNewPass.addClass("error");
            emptyField = true;
        }
        if($newPass.val() === $reNewPass.val() && !emptyField){
            var data = {"oldpass" : $oldPass.val(), "newpass" : $newPass.val()};
            $.ajax({
                url: "../rest/user/changepass",
                type: 'POST',
                data: data,
                success: passwordChangeSuccess,
                error: passwordChangeFailure,
            });
        }
    })
});
function passwordChangeSuccess(data) {
    $("#popup-title").text("Suksess");
    $("#popup-message").text("Ditt passord har nå blitt endret.");
    $("#popup-btn2").hide();
    $("#popup-btn1").text("Ok");
    $(".popup").show();
    $("#popup-btn1").click(function () {
        $(".popup").hide();
    })
}
function passwordChangeFailure(data) {
    $("#popup-title").text("En feil har oppstått");
    $("#popup-message").text("Passordet som ble skrevet inn samsvarer ikke med ditt passord, skriv inn på nytt.");
    $("#popup-btn2").hide();
    $("#popup-btn1").text("Ok");
    $(".popup").show();
    $("#popup-btn1").click(function () {
        $(".popup").hide();
    })
}