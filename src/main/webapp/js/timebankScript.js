/**
 * Created by AnitaKristineAune on 24.01.2017.
 */

$(document).ready(function () {

 /*
 not finished
  */
    $("#overtimeBtn").click(function () {
        window.location="/html/register-overtime.html";
    });

    $.ajax({
        url: "/rest/overtime",
        type: 'GET',
        success: displayHours,
        error: function (data) {
            var hours = $(".timebank");
            hours.append("<p>" + data + "</p>");
        }
    });


    $.ajax({
        url: "/rest/shift",
        type: 'GET',
        success: displayDate,

    });


    function displayHours(data) {
        var hours = $("#hours");
        hours.html("");

        $.each(data, function (index, element) {
            var html = `<div>${element.minutes}</div>`;
            hours.append(html);
        });
    }

    function displayDate(data){
        var date = $(".hours");
        date.html("");
        $.each(data, function (index, element) {
            var html =`<div>${element.date}</div>`;
            date.append(html);
        });
    }




});
