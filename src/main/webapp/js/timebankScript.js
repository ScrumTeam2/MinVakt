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


    function displayHours(data) {
        var overtimeHours = $(".overtime-hours");
        var absenceHours = $(".absence-hours");
        overtimeHours.html("");
        absenceHours.html("");

        var minutes = ${element.minutes};
        $.each(data, function (index, element) {

            var html = `<div> ${element.minutes}</div>`;

            if(html > 0){
                overtimeHours.append(html);
            } else {
                absenceHours.append(html);
            }


        });
    }
});

/*
 $.ajax({
 url: "/rest/shift/user",
 type: 'GET',
 success: displayDate,

 });

 function displayDate(data){
 var date = $(".hours");
 date.html("");
 $.each(data, function (index, element) {
 var html =`<div>${element.date} test</div>`;
 date.append(html);
 });
 }
 */

