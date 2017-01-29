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

function reformatType(type){
    var formattedType;
    switch (type){
        case "DAY":
            formattedType = "Dagvakt";
            break;
        case "EVENING":
            formattedType = "Kveldsvakt";
            break;
        case "NIGHT":
            formattedType = "Nattevakt";
            break;
        default:
            console.log("type not known", type);
    }
    return formattedType;
}

function reformatTime(type){
    var typeTime;
    switch (type){
        case "DAY":
            typeTime = "07:00-15:00";
            break;
        case "EVENING":
            typeTime = "15:00-23:00";
            break;
        case "NIGHT":
            typeTime = "23:00-07:00";
            break;
        default:
            console.log("type not known", type);
    }
    return typeTime;
}

function checkInput(){
    $('#timebankBtn').click(function(e){
        e.preventDefault();

        var formError = false;

        var $hours = $('#hours');
        var $minutes = $('#minutes');

        $hours.removeClass('error').parent().attr('data-content', '');
        $minutes.removeClass('error').parent().attr('data-content', '');

        if(!$hours.val() || $hours.val() < 0 || $minutes.val() > 23){
            $hours.addClass('error').parent().attr('data-content', 'Du må fylle inn timer.');
            formError = true;
        }
        if(!$minutes.val() || $minutes.val() < 0 || $minutes.val() > 59){
            $minutes.addClass('error').parent().attr('data-content', 'Du må fylle inn minutter.');
            formError = true;
        }

        var hoursVal = $hours.val();
        var minutesVal = $minutes.val();


        if(!formError){
            calcMinutes(hoursVal, minutesVal);
        }
    });
}

// calculate to minutes
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
    console.log("Post error");
}

function getShift(){
    var $shiftInfo = $('.shift-info');
    var shiftTime = reformatTime(type);
    var shiftType = reformatType(type);
    var shiftDate = convertDate(date);
    $shiftInfo.html(
        `<p class="lead">${shiftDate}</p>
        <p class="sub">${shiftType} ${shiftTime}</p>`
    );
}

function registerOvertime(minutes){
    var startTime;
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
        "shiftId": shiftId,
        "startTime": startTime,
        "minutes": minutes,
        "approved": false,
        "date": date,
        "type": type
    };
    sendTimebank(formData);
}

// popup content of submitted overtime
function popupContent(){
    var $popupCont = $('#content');

    var shiftType = reformatType(type);
    var shiftDate = convertDate(date);

    var hours = parseInt($('#hours').val());
    var minutes = parseInt($('#minutes').val());

    if(hours === 0){
        $popupCont.html(
            `<h3>Overtid sendt til godkjenning</h3>
                <p>${shiftType} - ${shiftDate}</p>
                <p>Overtid: ${minutes} minutter</p>`
        );
    } else if(hours === 1){
        $popupCont.html(
            `<h3>Overtid sendt til godkjenning</h3>
                <p>${shiftType} - ${shiftDate}</p>
                <p>Overtid: ${hours} time og ${minutes} minutter</p>`
        );
    }
    else{
        $popupCont.html(
            `<h3>Overtid sendt til godkjenning</h3>
                <p>${shiftType} - ${shiftDate}</p>
                <p>Overtid: ${hours} timer og ${minutes} minutter</p>`
        );
    }
    showPopup();
}

function showPopup(){
    var $popup = $('.popup');
    console.log("Post success");
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