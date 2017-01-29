/**
 * Created by ingvildbroen on 12.01.2017.
 */

$(document).ready(function(){

    if(sessionStorage.getItem("SessionId")){
        if(sessionStorage.getItem("SessionIdCat") === 'ADMIN'){
            window.location = "home-a.html";
            console.log("Already logged in as admin");
        } else{
            window.location = "home-e.html";
            console.log("Already logged in as employee");
        }
    } else{
        $.ajax({
            url: "/rest/session/check",
            type: 'GET',
            dataType: 'json',
            success: login,
            error: function(e) {
                //console.error(e);
            }
        });
    }

    $('#login').click(function(e){
        e.preventDefault();
        var emptyField = true;
        var $identificator = $('#identificator');
        var $password = $('#password');
        $('#login').html(`<div class="typing_loader"></div>`);

        if(!$identificator.val()){
            $identificator.addClass('error');
            emptyField = false;
        }

        if(!$password.val()){
            $password.addClass('error');
            emptyField = false;
        }

        if(emptyField){

            $.ajax({

                url: "/rest/session/login",
                type: 'POST',
                data: {
                    identificator: $identificator.val(),
                    password: $password.val()
                },
                success: login,
                error: invalid
            });
        }
    });
});

function login(data){
    console.log("Login", data.id);
    console.log("Login", data.category);
    sessionStorage.SessionId = data.id;
    sessionStorage.SessionIdCat = data.category;
    sessionStorage.SessionIdDept = data.deptId;
    sessionStorage.firstName = data.firstName;
    sessionStorage.lastName = data.lastName;

    var date = new Date();
    date.setTime(date.getTime() + (1000 * 60 * 60 * 2));
    var timeNow = date.getTime();
    sessionStorage.SessionExpires = timeNow;

    if(sessionStorage.getItem("SessionIdCat") === 'ADMIN'){
        console.log("admin");
        window.location = "home-a.html";
    } else{
        console.log("ansatt");
        window.location = "home-e.html";
    }
}

function invalid(data){
    setTimeout(function() {$('#login').resetLoader("Logg inn");}, 500);
    $('.feedback').show();
    console.log("Invalid", data);
}

//function to reset the loading button animation
$.fn.resetLoader = function(buttonText){
    $('.typing_loader').remove();
    $(this).text(buttonText);
};
