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
var absenceIds = [];
function addShiftInfoHtml (element, shiftId, data) {

    employeeCategories = {'ASSISTANT': 'Assistent', 'HEALTH_WORKER': 'Helsemedarbeider', 'NURSE': 'Sykepleier'};
    categoriesForLoop = ['ASSISTANT', 'HEALTH_WORKER', 'NURSE'];
    var shiftUsers = data.shiftUsers;
    var html = "";
    var iAmOnShift = false;
    var absence = 0;
    //Could be made more efficient
    var baseUrl = "../html/user-e.html?search=";
    console.log('aboveeee-aa-');
    for (var i = 0; i < categoriesForLoop.length; i++) {
        var hasPerson = false;
        //console.log(categoriesForLoop[i]);
        //console.log(html);
        var absence = 0;
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

            if(user.userId == sessionStorage.SessionId) {
                iAmOnShift = true;
            }
            switch (user.valid_absence2) {
                case 1:
                absence = 1;
                break;
                case 2:
                absence = 2;
                break;
            }
        });
    }
    //console.log(html);

    // Add shift change button + illness button
    if(iAmOnShift) {
        if(absence == 1 || absenceIds.indexOf(shiftId)>-1) {
            html +=
                /*'<div class="button-group"><button type="submit" onclick="regByttVakt();" id="regByttVakt">Bytt vakt</button><button type="submit" data-id="'+shiftId+'" onclick="regSykdom(this);" id="regSykdom">Du har registrert sykdom</button></div>';
                */
                '<div class="button-group"><button type="submit" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="regByttVakt();" id="regByttVakt">Bytt vakt</button><div class="dialogboks"><h3>Du har registrert sykdom</h3></div></div>';
                
        } else if(absence==2 || absenceIds.indexOf(shiftId)>-1) {
            html +=
                '<div class="button-group"><button type="submit" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="regByttVakt();" id="regByttVakt">Bytt vakt</button><div class="dialogboks"><h3>Ditt fravær for sykdom har blitt godkjent av betjening</h3></div></div>';
        }else {
            html +=
                '<div class="button-group"><button type="submit" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="regByttVakt();" id="regByttVakt">Bytt vakt</button><button type="submit" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="regSykdom(this);" id="regSykdom">Sykdom</button></div>';
        }
    }

    element.append(html);
}
var shiftSykdom = 0;
var prevDiv;
function regSykdom(that) {
    var shiftId = $(that).attr('data-id');
    shiftSykdom = shiftId;
    prevDiv = that;
    var date = $(that).attr('data-date');
    var staff = $(that).attr('data-staff');
    console.log(date);
    console.log(staff);
    console.log(absenceIds);
    if(absenceIds.indexOf(shiftId)>-1) {
        return $(that).html('<div class="dialogboks"><h3>Du har allerede registrert sykdom for denne vakten</h3></div>');
    }   
    displayPopup(shiftId,date,staff);
    function displayPopup(shiftId,date,staff) {
        $(".popup p").replaceWith("Er du sikker på at du vil registrere fravær for vakt den " + date + "? Det er meldt " + staff + " ansatte til denne vakten");
        $(".popup").show();
    }
    //moreInfoElement.slideToggle();
}

function poopStatus(status, that) {
    var shiftId = shiftSykdom;
    if (status) {
        $.ajax({
            url: "../rest/shift/user/valid_absence/" + shiftId,
            type: 'GET',
            dataType: 'json',
            success: successRegisterSykdom,
            error: function(data) {
                console.log(data)
            }
        });
        $(".popup").fadeOut();
        absenceIds.push(shiftId);
        $(prevDiv).replaceWith('<div class="dialogboks"><h3>Du har registrert sykdom</h3></div>');
    } else {
        $(".popup").fadeOut();
    }
}

function successRegisterSykdom(data) {
    console.log('succes');
    //console.log(data);
    console.log('succ responseTextBelow');
    console.log(data.responseText);
    //Popup
}

function convertDate(dateInput){
    var monthNames = [
        "januar", "februar",
        "mars", "april", "mai",
        "juni", "juli", "august",
        "september", "oktober", "november",
        "desember"];

    var dayNames = ["Søndag", "Mandag", "Tirsdag",
        "Onsdag", "Torsdag", "Fredag",
        "Lørdag"];

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
        if(element.date !== currentDate){
            currentDate = element.date;
            html =
                `<div class='container-title'>
                <h3>${convertDate(element.date)}</h3>
                </div>`;
        }
        html +=
            `<div class='watch'>
                <div class='watch-info'>
                    <p class='lead'>${shiftTypes[element.shiftType]}</p>
                    <p class='sub'>${shiftTimes[element.shiftType]}</p>
                </div>
                <i class='symbol info-button' data-id='${element.shiftId}'><i class='material-icons'>info_outlines</i></i>
                <div class='more-info'></div>
            </div>`;
        calendarList.append(html);
        html = "";
    });
    createInfoListeners();
}
function createInfoListeners() {
    $('.info-button').click(function (e) {
        var clickedElement = $(this);
        var moreInfoElement = clickedElement.next();
        //console.log(moreInfoElement);
        isLoaded = moreInfoElement.attr("loaded");
        //console.log("Attr: " +isLoaded);
        if (isLoaded == "true") {
            moreInfoElement.slideToggle();
            //console.log("Correct: "+ moreInfoElement.firstChild);
        }
        else {
            moreInfoElement.attr("loaded", "true");
            //console.log("Wrong"+moreInfoElement.firstChild);
            e.preventDefault();
            var shiftId = clickedElement.attr('data-id');
            //console.log("ShiftId = " + shiftId);
            $.ajax({
                url: "../rest/shift/" + shiftId,
                type: 'GET',
                dataType: 'json',
                success: function (data) {
                    addShiftInfoHtml(moreInfoElement, shiftId, data);
                },
                error: function (data) {
                    //console.log(data)
                }
            });
        }
    });
}

function createPeopleListeners() {
    $('.person').click(function (e) {
        var title;
        var icon = $(this).find(".material-icons");
        console.log(icon);
        if ($(this).hasClass("person")){
            icon.text("people");
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
                    //console.log("Error, no data found");
                    var calendarList = $(".list");
                    calendarList.append("<p>" + data + "</p>");
                }
            });
        }
        else {
            icon.text("person");
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

    $.each(data, function (index, element) {
        if(element.date !== currentDate) {
            currentDate = element.data;
            currentDate = element.date;
            html =
                "<div class='container-title'>" +
                "<h3>" + convertDate(element.date) + "</h3>" +
                "</div>";
        }
        html += "<div class='watch'>" +
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
        html = ""
    });
    createInfoListeners()
}
function createCalendarListener(){
    var calendarIcon = $("#today");
    calendarIcon.click(function () {
        var calendar = $("#calendar");
        calendar.slideToggle();
    });
}

