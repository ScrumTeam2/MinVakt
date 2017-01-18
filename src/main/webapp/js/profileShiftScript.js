/**
 * Created by evend on 1/12/2017.
 */
$(document).ready(function () {
    createPeopleListeners();
    createCalendarListener();
});
createAjaxForOwnShifts();
function createAjaxForOwnShifts() {
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
}
//});

function addShiftInfoHtml (element, shiftId, data) {

    employeeCategories = {'ASSISTANT': 'Assistent', 'HEALTH_WORKER': 'Helsemedarbeider', 'NURSE': 'Sykepleier'};
    categoriesForLoop = ['ASSISTANT', 'HEALTH_WORKER', 'NURSE'];
    var shiftUsers = data.shiftUsers;
    var hasPerson = false;
    var html = "";
    //Could be made more efficient
    var baseUrl = "../html/user-e.html?search=";
    for (var i = 0; i < categoriesForLoop.length; i++) {
        console.log(categoriesForLoop[i]);
        console.log(html);
        $.each(shiftUsers, function (index, user) {
            if (user.userCategory == categoriesForLoop[i]) {
                if (!hasPerson) {
                    html += "<h4>" + employeeCategories[categoriesForLoop[i]] + "</h4>";
                    hasPerson = true;
                }
                if (user.responsibility) {
                    html += "<a href='"+baseUrl+user.userName +
                        "' class='link'>" + user.userName + " (Vaktansvarlig)<i class='material-icons'>chevron_right</i></a>"
                }
                else {
                    html += "<a href='"+baseUrl+user.userName+"' class='link'>" + user.userName + "<i class='material-icons'>chevron_right</i></a>"
                }
            }
        });
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
    var html = "";
    var calendarList = $(".list");
    calendarList.html("");
    var shiftTypes = {"DAY" : "Dagvakt", "EVENING" : "Kveldsvakt", "NIGHT" : "Nattevakt"};
    var shiftTimes = {"DAY" : "07.00 - 15.00", "EVENING" : "15.00 - 23.00", "NIGHT" : "23.00 - 07.00"};
    var currentDate = "";
    $.each(data, function (index, element) {
        console.log(shiftTypes[element.shiftType]);
        if(element.date != currentDate){
            currentDate = element.date;
            html =
                "<div class='container-title'>" +
                "<h3>"+convertDate(element.date)+"</h3>" +
                "</div>";
        }
        html +=
            "<div class='watch'>" +
                "<div class='watch-info'>" +
                    "<p class='lead'>"+shiftTypes[element.shiftType]+"</p>" +
                    "<p class='sub'>"+shiftTimes[element.shiftType]+"</p>" +
                "</div>" +
                "<i class='symbol info-button' data-id='"+element.shiftId+"'><i class='material-icons'>info_outlines</i></i>" +
                "<div class='more-info'></div>" +
            "</div>";
        calendarList.append(html);
        html = "";
    });
    createInfoListeners();
}
function createInfoListeners() {
    $('.info-button').click(function (e) {
        var clickedElement = $(this);
        var moreInfoElement = clickedElement.next();
        console.log(moreInfoElement);
        isLoaded = moreInfoElement.attr("loaded");
        console.log("Attr: " +isLoaded);
        if (isLoaded == "true") {
            moreInfoElement.toggle();
            console.log("Correct: "+ moreInfoElement.firstChild);
        }
        else {
            moreInfoElement.attr("loaded", "true");
            console.log("Wrong"+moreInfoElement.firstChild);
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

function createPeopleListeners() {
    $('.person').click(function (e) {
        var title;
        if ($(this).hasClass("person")){
            $(this).text("people");
            $(this).removeClass("person");
            $(this).addClass("people");
            title = $(".my-shifts");
            title.removeClass("my-shifts");
            title.addClass("all-shifts");
            title.text("Alle vakter");
            $.ajax({
                //     url: "rest/shift/user/"+userId,
                url: "../rest/shift",
                data: {daysForward : 300}, //TODO: edit to 7?
                type: 'GET',
                dataType: 'json',
                success: createAllShiftsHtml,
                error: function (data) {
                    console.log("Error, no data found");
                    var calendarList = $(".list");
                    calendarList.append("<p>" + data + "</p>");
                }
            });
        }
        else {
            $(this).text("person");
            $(this).removeClass("people");
            $(this).addClass("person");
            title = $(".all-shifts");
            title.removeClass("all-shifts");
            title.addClass("my-shifts");
            title.text("Mine vakter");
            createAjaxForOwnShifts();
        }
    })
}
function createAllShiftsHtml(data) {
    var calendarList = $(".list");
    calendarList.html("");
    var shiftTypes = {"DAY" : "Dagvakt", "EVENING" : "Kveldsvakt", "NIGHT" : "Nattevakt"};
    var shiftTimes = {"DAY" : "07.00 - 15.00", "EVENING" : "15.00 - 23.00", "NIGHT" : "23.00 - 07.00"};
    var currentDate = "";
    var html = "";
    console.log(data);
    $.each(data, function (index, element) {
        if(element.date != currentDate) {
            currentDate = element.date;

            html =
                "<div class='container-title'>" +
                "<h3>" + convertDate(element.date) + "</h3>" +
                "</div>";
        }
        html = "<div class='watch'>" +
        "<div class='watch-info'>" +
        "<p class='lead'>" + shiftTypes[element.shiftType] + "</p>" +
        "<p class='sub'>" + shiftTimes[element.shiftType] + "</p>" +
        "</div>";
        if (element.hasUser) {
            html +=
                "<div class='watch-info'>" +
                "<p class='sub'>Din vakt</p>" +
                "</div>";
        }
        else if (element.available) {
            html +=
                "<div class='watch-info'>" +
                "<p class='sub'>Ledig vakt</p>" +
                "</div>";
        }
        html +=
            "<i class='symbol info-button' data-id='" + element.shiftId + "'><i class='material-icons'>info_outlines</i></i>" +
            "<div class='more-info'></div></div>";
        calendarList.append(html);
    });
    createInfoListeners()
}
function createCalendarListener(){
    var calendarIcon = $("#today");
    calendarIcon.click(function () {
        var calendar = $("#calendar");
        calendar.toggle();
    });
}

