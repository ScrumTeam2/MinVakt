/**
 * Created by evend on 1/12/2017.
 */
$(document).ready(function () {
    createAjaxForAllShifts();
    createCalendarListener();

});
function createAjaxForAllShifts() {
    $.ajax({
        //     url: "rest/shift/user/"+userId,
        url: "../rest/shift",
        data: {daysForward : 7}, //TODO: edit to 7?
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
//});
var absenceIds = [];
var shiftChangeIds = [];
var shiftRemovedIds = [];
function addShiftInfoHtml (element, shiftId, data) {

    employeeCategories = {'ASSISTANT': 'Assistent', 'HEALTH_WORKER': 'Helsemedarbeider', 'NURSE': 'Sykepleier'};
    categoriesForLoop = ['ASSISTANT', 'HEALTH_WORKER', 'NURSE'];
    var shiftUsers = data.shiftUsers;
    var html = "";
    var iAmOnShift = false;
    var absence = 0;
    //Could be made more efficient
    var shiftChange = false;
    var removed = false;
    var baseUrl = "../html/user-e.html?search=";
    console.log('aboveeee-aa-');
    for (var i = 0; i < categoriesForLoop.length; i++) {
        var hasPerson = false;
        //console.log(categoriesForLoop[i]);
        //console.log(html);
        var absence = 0;
        var shiftChange = false;
        $.each(shiftUsers, function (index, user) {
            if (user.userCategory == categoriesForLoop[i]) {
                if (!hasPerson) {
                    html += "<h4>" + employeeCategories[categoriesForLoop[i]] + "</h4>";
                    hasPerson = true;
                }
                if (user.responsibility) {
                    html += "<a href='"+baseUrl+user.userName +
                        "' class='link'>" + user.userName + " (Ansvarsvakt)<i class='material-icons'>chevron_right</i></a>"
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
            switch (user.shift_change) {
                case true:
                    shiftChange = true;
                    console.log(shiftId);
                    console.log('set to true');
                    break;
            }
            switch (user.removed) {
                case true:
                    removed = true;
                    break;
            }
        });
    }
    //console.log(html);

    // Add shift change button + illness button
    if(iAmOnShift) {
        if(removed) {
            html +='<div class="button-group"><p>Denne vakten er ikke lenger din</div>';
        } else {
            if(absence == 1 || absenceIds.indexOf(shiftId)>-1) {
                if(shiftChange == true) {
                    console.log(shiftId);
                    console.log('----SHIFT CHANGE IS ALSO TRUE1----');
                    html+='<div class="button-group"><div class="dialogboks"><h3>Du har registrert vaktbytte</h3></div></div>';
                } else {
                html +=
                    /*'<div class="button-group"><button type="submit" onclick="regByttVakt();" id="regByttVakt">Bytt vakt</button><button type="submit" data-id="'+shiftId+'" onclick="registerIllness(this);" id="registerIllness">Du har registrert sykdom</button></div>';
                    */
                    '<div class="button-group"><div class="dialogboks"><h3>Du har registrert sykdom</h3></div></div>';
                }
            } else if(absence==2 || absenceIds.indexOf(shiftId)>-1) {
                if(shiftChange == true) {
                    console.log('----SHIFT CHANGE IS ALSO TRUE2----');
                    console.log(shiftId);
                    html+='<div class="button-group"><div class="dialogboks"><h3>Du har registrert vaktbytte</h3></div></div>';
                } else {
                html +=
                    '<div class="button-group"><div class="dialogboks"><h3>Ditt fravær for sykdom har blitt godkjent av betjening</h3></div></div>';
                }
            }else {
                if(shiftChange == true) {
                    console.log(shiftId);
                    console.log('----SHIFT CHANGE IS ALSO TRUE3----');
                    html+='<div class="button-group"><div class="dialogboks"><h3>Du har registrert vaktbytte</h3></div></div>';
                } else {
                html +=
                    '<div class="button-group"><button type="submit" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="reqChangeShift(this);" id="reqChangeShift">Bytt vakt</button><button type="submit" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="registerIllness(this);" id="registerIllness">Registrer Sykdom</button></div>';
                }
            }
        }
    }

    element.append(html);
}
var shiftIllness = 0;
var prevDiv;
var reqChangeShiftId = 0;
var reqPrevDiv;
function registerIllness(that) {
    var shiftId = $(that).attr('data-id');
    shiftIllness = shiftId;
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
        $("#registerIllnessPopup p").replaceWith("Er du sikker på at du vil registrere fravær for vakt den " + date + "? Det er meldt " + staff + " ansatte til denne vakten");
        $("#registerIllnessPopup").show();
    }
    //moreInfoElement.slideToggle();
}

function poopStatus(status, that) {
    var shiftId = shiftIllness;
    if (status) {
        $.ajax({
            url: "../rest/shift/user/valid_absence/" + shiftId,
            type: 'GET',
            dataType: 'json',
            success: successRegisterIllness,
            error: function(data) {
                console.log(data)
            }
        });
        $("#registerIllnessPopup").fadeOut();
        absenceIds.push(shiftId);
        $(prevDiv).parent().replaceWith('<div class="dialogboks"><h3>Du har registrert sykdom</h3></div>');
    } else {
        $("#registerIllnessPopup").fadeOut();
    }
}

function successRegisterIllness(data) {
    console.log('success register illness');


    console.log(data.responseText);
    //Popup
}

function reqChangeShift(that) {
    var shiftId = $(that).attr('data-id');
    reqChangeShiftId = shiftId;
    reqPrevDiv = that;
    var date = $(that).attr('data-date');
    var staff = $(that).attr('data-staff');
    console.log(date);
    console.log(staff);
    console.log(shiftChangeIds);
    if(shiftChangeIds.indexOf(shiftId)>-1) {
        return $(that).html('<div class="dialogboks"><h3>Du har allerede registrert vaktbytte for denne vakten</h3></div>');
    }   
    displayShiftPopup(shiftId,date,staff);
    function displayShiftPopup(shiftId,date,staff) {
        $("#changeShiftPopup p").replaceWith("Er du sikker på at du vil registrere vaktbytte for vakt den " + date + "? Det er meldt " + staff + " ansatte til denne vakten");
        $("#changeShiftPopup").show();
    }
    //moreInfoElement.slideToggle();
}

function changeShiftStatus(status, that) {
    var shiftId = reqChangeShiftId;
    if (status) {
        $.ajax({
            url: "../rest/shift/user/shift_change/" + shiftId,
            type: 'GET',
            dataType: 'json',
            success: function success(data) {
            },
            error: function(data) {
                console.log(data)
            }
        });
        $("#changeShiftPopup").fadeOut();
        shiftChangeIds.push(shiftId);
        $(reqPrevDiv).parent().replaceWith('<div class="dialogboks"><h3>Du har registrert vaktbytte</h3></div>');
    } else {
        $("#changeShiftPopup").fadeOut();
    }
}
/*
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
*/
function createInfoListeners() {
    $('.clickable').click(function (e) {
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
/*
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
}*/
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
        html += "<div class='clickable cursor-point' data-id='" + element.shiftId + "'><div class='watch'>" +
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
            "<i class='symbol info-button' ><i class='material-icons'>info_outlines</i></i>" +
            "</div></div><div class='more-info'></div>";
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

