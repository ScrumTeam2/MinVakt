/**
 * Created by AnitaKristineAune on 26.01.2017.
 */

/* Admin can register absence for given user
Not finished
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

        if(!formError){
            calcMinutes(hoursVal, minutesVal);
        }
    });
}

function calcMinutes(hoursVal, minutesVal){
    var minutes = -((hoursVal * 60) + minutesVal);
    registerAbsence(minutes);
}

function sendTimebank(formData){
    $.ajax({
        url: "/rest/overtime",
        type: 'POST',
        dataType: 'json',
        contentType: "application/json",
        data: JSON.stringify(formData),
        success: function(){
            popupContent();
        },
        error: function(e){
            console.error(e);
        }
    });
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

function registerAbsence(minutes){
    var startTime;

    switch (type){
     case "DAY":
     startTime = 420;
     break;
     case "EVENING":
     startTime = 900;
     break;
     case "NIGHT":
     startTime = 1380;
     break;
     default:
     console.error("Unknown type", type);
     }
    var formData;
    formData = {
        "userId": -1,
        "shiftId": 2,
        "startTime": 900,
        "minutes": minutes,
        "approved": false,
        "date": "2017-01-23",
        "type": "DAY"
    };
    sendTimebank(formData);
}

function popupContent(){
    var $popup = $('.popup');

    var message1 = "Mandag 19. januar 2017";
    var message2 = "Dagvakt 07:00-15:00";
    var hours = $('#hours').val();
    var minutes = $('#minutes').val();

    if(hours === 0){
        $popup.html(
            `<h3>Overtid sendt til godkjenning</h3>
                <p>Vakt: ${message1} - ${message2}</p>
                <p>Overtid: ${minutes} minutter</p>`
        );
    } else{
        $popup.html(
            `<h3>Overtid sendt til godkjenning</h3>
                <p>Vakt: ${message1} - ${message2}</p>
                <p>Overtid: ${hours} timer og ${minutes} minutter</p>`
        );
    }
    showPopup();
}

function showPopup(){
    var $popup = $('.popup');
    $popup.show();

}

function hidePopup() {
    var $popup = $('.popup');
    $popup.hide();
}