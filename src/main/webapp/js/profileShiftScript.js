/**
 * Created by evend on 1/12/2017.
 */
$(document).ready(
    $.ajax({
   //     url: "rest/shift/user/"+userId,
        url: "../rest/shift/user/1",
        type: 'GET',
        dataType: 'json',
        success: createUserShiftHtml,
        error: function (data) {
            var calendarList = $(".calendar-list");
            calendarList.append("<p>"+data+"</p>");
        }
    })
);


function convertDate(dateInput){
    var monthNames = [
        "januar", "februar",
        "mars", "april", "mai",
        "juni", "juli", "august",
        "september", "oktober", "november",
        "desember"];

    var dayNames = ["Mandag", "Tirsdag",
        "Onsdag", "Torsdag", "Fredag",
        "Lørdag", "Søndag"];

    var date = new Date(dateInput);
    var day = date.getDate();
    var monthIndex = date.getMonth();
    var dayIndex = date.getDay();

    return dayNames[dayIndex] + " " + day + ". " + monthNames[monthIndex];

}

function createUserShiftHtml(data) {
    var calendarList = $(".calendar-list");
    var shiftTypes = {"DAY" : "Dagvakt", "EVENING" : "Kveldsvakt", "NIGHT" : "Nattevakt"};
    var shiftTimes = {"DAY" : "07.00 - 15.00", "EVENING" : "15.00 - 23.00", "NIGHT" : "23.00 - 07.00"};
    $.each(data, function (index, element) {
        var html =
            "<div class='container-title'>" +
                "<h3>"+convertDate(element.date)+"</h3>" +
            "</div>" +
            "<div class='watch'>" +
                "<div class='watch-info'>" +
                    "<p class='lead'>"+shiftTypes[element.shiftType]+"</p>" +
                    "<p class='sub'>"+shiftTimes[element.shiftType]+"</p>" +
                "</div>" +
                "<i class='symbol info-icon'><i class='material-icons'>info_outlines</i></i>" +
            "</div>";
        calendarList.append(html)
    })
}

