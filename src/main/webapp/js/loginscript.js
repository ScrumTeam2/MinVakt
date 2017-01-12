/**
 * Created by ingvildbroen on 12.01.2017.
 */


$('#login').click(function(e){
    e.preventDefault();
    $.ajax({
        url: "rest/session/login",
        type: 'POST',
        data: '{}',
        dataType: 'json',
        success: login(data),
        error: invalid
    });
});


function login(data){
    var identificator = $('#identificator');
    var password = $('#password');

    //console.log(data);

    //go to homepage
    window.location="index.html";
}

function invalid(data){
    var identificator = $('#identificator');
    var password = $('#password');

    identificator.addClass('error');
    password.addClass('error');
    $('.feedback').show();
    alert(data)
}