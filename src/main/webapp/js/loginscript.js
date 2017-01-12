/**
 * Created by ingvildbroen on 12.01.2017.
 */

$(document).ready(function(){

    var formData = JSON.stringify($("#form").serializeArray());
    $('#login').click(function(){
        $.ajax({
            url: "/rest/session/login",
            type: 'POST',
            dataType: 'json',
            data: formData,
            success: login,
            error: invalid
        });
    });
});


function login(data){

    //go to homepage
    window.location="index.html";
}

function invalid(data){
    var identificator = $('#identificator');
    var password = $('#password');

    identificator.addClass('error');
    password.addClass('error');
    $('.feedback').show();
    alert(data);
}