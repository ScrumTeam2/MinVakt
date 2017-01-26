/**
 * Created by AnitaKristineAune on 24.01.2017.
 */
var shiftId = getUrlParameter("shiftId");
var date = getUrlParameter("date");
var type = getUrlParameter("type");

$(document).ready(function () {
    getShift();

    var $hours = $('#hours');
    var $minutes = $('#minutes');

    $hours.removeClass('error').parent().attr('data-content', '');
    $minutes.removeClass('error').parent().attr('data-content', '');

    checkInput();
});


function checkInput(){
    $('#timebankBtn').click(function(e){
        e.preventDefault();

        console.log("klikket");

        var formError = false;

        var $hours = $('#hours');
        var $minutes = $('#minutes');

        $hours.removeClass('error').parent().attr('data-content', '');
        $minutes.removeClass('error').parent().attr('data-content', '');

        if(!$hours.val()){
            $hours.addClass('error').parent().attr('data-content', 'Du må fylle inn timer.');
            formError = true;
        }
        if(!$minutes.val()){
            $minutes.addClass('error').parent().attr('data-content', 'Du må fylle inn minutter.');
            formError = true;
        }

        var hoursVal = $hours.val();
        var minutesVal = $minutes.val();

        console.log(formError);

        if(!formError){
            calcMinutes(hoursVal, minutesVal);
        }
    });
}

function calcMinutes(hoursVal, minutesVal){
    var parsedH = parseInt(hoursVal);
    var parsedM = parseInt(minutesVal);
    var minutes = (parsedH * 60) + parsedM;
    registerOvertime(minutes);
}

function sendTimebank(formData){
    $.ajax({
        url: "/rest/overtime",
        type: 'POST',
        contentType: "application/json",
        data: JSON.stringify(formData),
        success: popupContent,
        error: errorMessage
    });
}

function errorMessage(e){
    console.log("ikke ok post", e);
    console.error("error", e);
}

function getShift(){
    var $shiftInfo = $('.shift-info');
    var message1 = "Mandag 19. januar 2017";
    var message2 = "Dagvakt 07:00-15:00";
    $shiftInfo.html(
        `<p class="lead">${message1}</p>
        <p class="sub">${message2}</p>`
    );

}

function registerOvertime(minutes){
    var startTime;
    var type = "DAY";
    switch (type){
        case "DAY":
            startTime = 900;
            break;
        case "EVENING":
            startTime = 1380;
            break;
        case "NIGHT":
            startTime = 420;
            break;
        default:
            console.log("type not known", type);
    }
    var formData;
    formData = {
        "userId": 1,
        "shiftId": 43,
        "startTime": startTime,
        "minutes": minutes,
        "approved": false,
        "date": "2017-02-06",
        "type": type
    };
    sendTimebank(formData);
}

function popupContent(){
    var $popupCont = $('#content');

    var message1 = "Mandag 19. januar 2017";
    var message2 = "dagvakt";
    var hours = parseInt($('#hours').val());
    var minutes = parseInt($('#minutes').val());

    if(hours === 0){
        $popupCont.html(
            `<h3>Overtid sendt til godkjenning</h3>
                <p>${message1}, ${message2}</p>
                <p>Overtid: ${minutes} minutter</p>`
        );
    } else if(hours === 1){
        $popupCont.html(
            `<h3>Overtid sendt til godkjenning</h3>
                <p>${message1}, ${message2}</p>
                <p>Overtid: ${hours} time og ${minutes} minutter</p>`
        );
    }
    else{
        $popupCont.html(
            `<h3>Overtid sendt til godkjenning</h3>
                <p>${message1}, ${message2}</p>
                <p>Overtid: ${hours} timer og ${minutes} minutter</p>`
        );
    }
    showPopup();
}

function showPopup(){
    var $popup = $('.popup');
    console.log("ok post");
    $popup.show();
}

$('#closeBtn').click(function(e) {
    e.preventDefault();
    redirectToHome();
});

function redirectToHome(){
    var $popup = $('.popup');
    $popup.hide();
    window.location = "home-e.html";
}