$(document).ready(function () {
    $("#userBtn").click(function (e) {
        e.preventDefault();
        var formError = false;

        var $oldPass = $("#oldPass");
        var $newPass = $("#newPass");
        var $reNewPass = $("#reNewPass");

        $oldPass.removeClass('error').parent().attr('data-content', '');
        $newPass.removeClass('error').parent().attr('data-content', '');
        $reNewPass.removeClass('error').parent().attr('data-content', '');

        if (!$oldPass.val()) {
            $oldPass.addClass("error").parent().attr('data-content', 'Du må fylle inn gammelt passord.');
            formError = true;
        }
        if (!$newPass.val()) {
            $newPass.addClass("error").parent().attr('data-content', 'Du må fylle inn nytt passord.');
            formError = true;
        }
        if (!$reNewPass.val()) {
            $reNewPass.addClass("error").parent().attr('data-content', 'Du må repetere nytt passord.');
            formError = true;
        }
        if ($newPass.val() != $reNewPass.val()) {
            $reNewPass.addClass('error').parent().attr('data-content', 'Må samsvare med nytt passord.');
            formError = true;
        }

        var errorMessage = validatePassword($newPass.val());
        if (errorMessage != null) {
            $newPass.addClass('error').parent().attr('data-content', errorMessage);
            formError = true;
        }

        if (!formError) {
            $('#userBtn').html(`<div class="typing_loader"></div>`);

            var data = {"oldpass": $oldPass.val(), "newpass": $newPass.val()};
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
    $('#userBtn').text("Endre passord");
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
    $('#userBtn').text("Endre passord");
    $("#popup-title").text("En feil har oppstått");
    $("#popup-message").text("Passordet som ble skrevet inn samsvarer ikke med ditt passord, skriv inn på nytt.");
    $("#popup-btn2").hide();
    $("#popup-btn1").text("Ok");
    $(".popup").show();
    $("#popup-btn1").click(function () {
        $(".popup").hide();
    })
}

function validatePassword(password) {
    if (password.length < 8) {
        return "Passordet må være minst 8 tegn";
    }

    if (password.toUpperCase() == password) {
        // No lowercase letters
        return "Passordet må ha både små og store bokstaver";
    }
    if (password.toLowerCase() == password) {
        // No uppercase letters
        return "Passordet må ha både små og store bokstaver";
    }

    if (getSpecialCharacterCount(password) < 2) {
        return "Passordet må ha minst 2 spesialtegn";
    }
    return null;
}

function getSpecialCharacterCount(string) {
    var specialChars = "0123456789<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=";
    var count = 0;
    for (var i = 0; i < string.length; i++) {
        if (specialChars.indexOf(string[i]) > -1) {
            count++;
        }
    }
    return count;
}