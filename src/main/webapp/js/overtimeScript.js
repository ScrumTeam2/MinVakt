/**
 * Created by AnitaKristineAune on 24.01.2017.
 */

/*
not finished
 */
/*
$(document).ready(function () {



    function isChecked(e) {
        var overtimeValue = $(".overtime-type:checked").val();

        if(overtimeValue === "absence"){
            $("#overtime-h").hide();
        } else{
            $("#absence-h").hide();
        }
    }

}
*/


/*
    var user = $.urlParam('userId');
    var userNr;
    $.getJSON(('/rest/overtime/' + user, function (data) {
        userNr = data.name;

    }


    function absence(minutes) {
        var overtimeValue = $(".overtime-type:checked").val();

        if(overtimeValue === "absence"){
            minutes *= -1;
        } else{
            minutes *= 1;
        }
     return minutes;
    }


    $('#userBtn').click(function(e){
        e.preventDefault();

        var emptyField = false;

        var $date = $('#date');
        var $time = $('#time');
        var $minutes = $('#minutes');

        if(!$date.val()){
            $date.addClass('error');
            emptyField = true;
        }

        if(!$time.val()){
            $time.addClass('error');
            emptyField = true;
        }

        if(!$minutes.val()){
            $minutes.addClass('error');
            emptyField = true;
        }

        var formData;

        if(overtimeValue === "overtime"){
            if(!emptyField){
                formData = {
                    "userId": ,
                    "shiftId": ,
                    "startTime": $time.val(),
                    "minutes": $minutes.val(),
                    "approved": false,
                };
                console.log(JSON.stringify(formData));
            }
        }
        */
/*
 private int userId;
 private int shiftId;
 private int startTime;
 private int minutes;
 private boolean approved;
 */
/*
        }

        var available;
        var date;
        var hasUser;
        var shiftId;
        var shiftType;
        $.ajax({
            url: "/rest/shift/{shiftId}",
            type: 'GET',
            dataType: "json",
            contentType: "application/json",
            data: {
                "available": available,
                "date": date,
                "hasUser": hasUser,
                "shiftId": shiftId,
                "shiftType": shiftType,
            }
        })

        $.ajax({
            url: "/rest/{userId}",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: addOvertime,
            error: invalidField
        });

    });

});

function addOvertime(){
    createSuccess = true;
    $('.title').text("Vellykket!");
    $('.result').text("Overtid sendt inn til godkjenning");

    $('.popup').show();
}

function invalidField(data){
    $('.title').text("Feil");

    if(data.responseJSON == null) {
        $('.result').text("En uventet feil oppsto");
    } else {
        $('.result').text(data.responseJSON.error);
    }

    $('#userViewBtn').hide();
    $('.popup').show();

}*/

