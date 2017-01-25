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

        $.each(data, function (index, element) {

            if(result > 0){
                var html = `<div class="container-title">${element.date}</div>`;
                html += `<div> ${element.minutes}</div>`;

                overtimeHours.append(html);
            } else {
                var html = `<div class="container-title">${element.date}</div>`;
                html += `<div> ${element.minutes}</div>`;

                absenceHours.append(html);
            }


        });
    }
});


