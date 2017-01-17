/**
 * Created by ingvildbroen on 12.01.2017.
 */

$(document).ready(function(){

    $('#login').click(function(e){
        e.preventDefault();
        var emptyField = true;
        var $identificator = $('#identificator');
        var $password = $('#password');

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
                    identificator: $("#identificator").val(),
                    password: $("#password").val()
                },
                success: login,
                error: invalid
            });
        }
    });
});

function login(data){
    console.log("Login", data);

    sessionStorage.SessionId = data.id;

    var date = new Date();
    date.setTime(date.getTime() + (1000 * 60));
    var timeNow = date.getTime();
    sessionStorage.SessionExpires = timeNow;

    if(data.category === 0){
        console.log("admin");
        window.location="home-a.html";
    } else{
        console.log("ansatt");
        window.location="home-e.html";
    }
}

function invalid(data){
    $('.feedback').show();
    console.log("Invalid", data);
}