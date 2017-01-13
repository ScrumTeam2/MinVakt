$('#godkjennVaktbyte').click(function(e){
    var userChange = $('#user1');
    var userChanger = $('#user2');
    var shiftId = $('shiftId').value();
    e.preventDefault();
    $.ajax({
        url: "rest/session/vaktbytte",
        type: 'POST',
        data: {
            user1: userChange,
            user2: userChanger,
            shiftId: shiftId
        },
        dataType: 'json',
        success: vaktbytte,
        error: invalid
    });
});

$('#avbrytVaktbyte').click(function(e){
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

function vaktbytte(data){
    if(data.result == "accepted") {
        $('accepted').fadeIn();
    } else {
        $('avlyst').fadeIn();
    }
}

function invalid(data){
    /*var identificator = $('#identificator');
    var password = $('#password');

    identificator.addClass('error');
    password.addClass('error');
    */
    $('.feedback').show();
    alert(data)
}