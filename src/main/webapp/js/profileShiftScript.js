/**
 * Created by evend on 1/12/2017.
 */
//$(document).ready(function () {
    $.ajax({
        //     url: "rest/shift/user/"+userId,
        url: "../rest/shift/user",
        type: 'GET',
        dataType: 'json',
        success: createUserShiftHtml,
        error: function (data) {
            var calendarList = $(".list");
            calendarList.append("<p>" + data + "</p>");
        }
    });
//});

function addShiftInfoHtml (element, shiftId, data) {

    employeeCategories = {'ASSISTANT': 'Assistent', 'HEALTH_WORKER': 'Helsemedarbeider', 'NURSE': 'Sykepleier'};
    categoriesForLoop = ['ASSISTANT', 'HEALTH_WORKER', 'NURSE'];
    var shiftUsers = data.shiftUsers;
    var hasPerson = false;
    var html = "";
    //Could be made more efficient
    for (var i = 0; i < categoriesForLoop.length; i++) {
        console.log(categoriesForLoop[i]);
        console.log(html);
        $.each(shiftUsers, function (index, user) {
            console.log(user);
            if (user.userCategory == categoriesForLoop[i]) {
                if (!hasPerson) {
                    html += "<h4>" + employeeCategories[categoriesForLoop[i]] + "</h4>";
                    hasPerson = true;
                }
                if (user.responsibility) {
                    html += "<a href='#' class='link'>" + user.userName + " (Vaktansvarlig)<i class='material-icons'>chevron_right</i></a>"
                }
                else {
                    html += "<a href='#' class='link'>" + user.userName + "<i class='material-icons'>chevron_right</i></a>"
                }
            }
            console.log(html);

        });
        console.log(html);
        console.log(element);
        element.append(html);

    }
    console.log(html);
    element.append(html);
}


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
    var calendarList = $(".list");
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
                "<i class='symbol info-button' data-id='"+element.shiftId+"'><i class='material-icons'>info_outlines</i></i>" +
            "</div>" +
            "<div class='more-info'></div>";
        calendarList.append(html)
    });
    createInfoListeners();
}
function createInfoListeners() {
    $('.info-button').click(function (e) {
        clickedElement = $(this);
        moreInfoElement = clickedElement.closest('.more-info');
        if (moreInfoElement.length != 0) {
            moreInfoElement.toggle();
        }
        else {
            e.preventDefault();
            var shiftId = clickedElement.attr('data-id');
            console.log("ShiftId = " + shiftId);
            $.ajax({
                url: "../rest/shift/" + shiftId,
                type: 'GET',
                dataType: 'json',
                success: function (data) {
                    addShiftInfoHtml(moreInfoElement, shiftId, data);
                },
                error: function (data) {
                    console.log(data)
                }
            });
        }
    });
}

