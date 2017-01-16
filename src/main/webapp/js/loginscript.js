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


    if(data.isAdmin === true){
        console.log("admin");
        window.location="home-a.html";
    } else{
        console.log("ansatt");
        window.location="home-e.html";
    }
}

function invalid(data){
    $('.feedback').fadeIn(1500);
    console.log("Invalid", data);
}