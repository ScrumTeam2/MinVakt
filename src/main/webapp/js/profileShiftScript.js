/**
 * Created by evend on 1/12/2017.
 */
$(document).ready(function () {
    createAjaxForAllShifts();
    createCalendarListener();
    setDeptOptions();

});
function setDeptOptions() {
    var html;
    $.ajax({
        //     url: "rest/shift/user/"+userId,
        url: "../rest/department/withData",
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            var $dropdown = $("#dept-options");
            $.each(data, function (index, dept) {
                console.log(sessionStorage.getItem("SessionIdDept"));
                if(dept.id == sessionStorage.getItem("SessionIdDept")) {
                    $dropdown.append("<option selected name='category' class='dept-category' id = '"+dept.id+"' value='" + dept.id + "'>" + dept.name +
                        "</option>");
                }
                else{
                    $dropdown.append("<option name='category' data-id = '"+dept.id+"' class='dept-category' id = 'dept"+dept.id+"' value='" + dept.id + "'>" + dept.name +
                        "</option>");
                }
                if(dept.hasAvailable){
                    $dropdown.children("#dept"+dept.id).append("<span class='circle green'></span>")
                }
                if(dept.hasUser){
                    $dropdown.children("#dept"+dept.id).append("<span class='circle blue'></span>")
                }
            });
            $dropdown.change(function (e) {
                var $selected = $("select option:selected");
                var deptId = $selected.attr("data-id");
                departmentId = deptId;

                e.preventDefault();
                createAjaxForAllShifts(deptId);
                createCalendatWithData(); // Refresh calendar
            })
        },
        error: function (data) {
            //console.log("Error, no data found");
            var calendarList = $(".list");
            calendarList.append("<p>" + data + "</p>");
        }
    });
}
function createAjaxForAllShifts(deptId) {
    console.log("departmentId: " + departmentId);
    var data;
    console.log(deptId);
    if(!deptId || deptId < 1){
        data = {daysForward: 7}
    }
    else {
        data = {daysForward: 7, "deptId" : deptId}
    }
    if(currentDate !== undefined) {
        data["date"] = currentDate;
    }
    console.log(data);
    $.ajax({
        //     url: "rest/shift/user/"+userId,
        url: "../rest/shift",
        data: data, //TODO: edit to 7?
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
    var removed = false;
    var shiftChange = false;

    //Could be made more efficient
    var baseUrl = "../html/user-e.html?search=";
    for (var i = 0; i < categoriesForLoop.length; i++) {
        var hasPerson = false;

        $.each(shiftUsers, function (index, user) {

            //console.log("Loop", index, user);

            if (user.userCategory == categoriesForLoop[i]) {
                if (!hasPerson) {
                    html += "<h4>" + employeeCategories[categoriesForLoop[i]] + "</h4>";
                    hasPerson = true;
                }
                if (user.responsibility) {
                    html += "<a href='" + baseUrl + user.userName +
                        "' class='link'>" + user.userName + " (Ansvarsvakt)<i class='material-icons'>chevron_right</i></a>"
                }
                else {
                    html += "<a href='" + baseUrl + user.userName + "' class='link'>" + user.userName + "<i class='material-icons'>chevron_right</i></a>"
                }
            }

            if (user.userId == sessionStorage.SessionId) {
                iAmOnShift = true;
                shiftChange = user.shift_change;
                switch (user.valid_absence) {
                    case 1:
                        absence = 1;
                        break;
                    case 2:
                        absence = 2;
                        break;
                }
                switch (user.removed) {
                    case true:
                        removed = true;
                        break;
                }
            }

        });
    }

    // Add shift change button + illness button
    if (iAmOnShift) {
        if (absence > 0) {
            html += '<div class="button-group"><button class="btn-secondary" disabled>Du har registrert sykdom</button></div>';
        } else if (shiftChange) {
            html += '<div class="button-group"><button class="btn-secondary" disabled>Du har registrert vaktbytte</button></div>';
        } else {
            if (data.date < Date.now()) {
                html +=
                    '<div class="button-group"><button type="submit" data-date="' + data.date + '" data-staff="' + data.staffNumb + '" data-id="' + shiftId + '" onclick="reqChangeShift(this);" id="regByttVakt" disabled>Bytt vakt</button>' +
                    '<button type="submit" data-date="' + data.date + '" data-staff="' + data.staffNumb + '" data-id="' + shiftId + '" onclick="registerIllness(this);" id="regSykdom" disabled>Registrer sykdom</button>' +
                    '<button type="submit" data-time="' + data.type + '" data-date="' + data.date + '" data-staff="' + data.staffNumb + '" data-id="' + shiftId + '" onclick="regOvertime(this);" id="regOvertime">Registrer overtid</button></div>';
            } else {
                html +=
                    '<div class="button-group"><button type="submit" data-date="' + data.date + '" data-staff="' + data.staffNumb + '" data-id="' + shiftId + '" onclick="reqChangeShift(this);" id="regByttVakt">Bytt vakt</button>' +
                    '<button type="submit" data-date="' + data.date + '" data-staff="' + data.staffNumb + '" data-id="' + shiftId + '" onclick="registerIllness(this);" id="regSykdom">Registrer sykdom</button>' +
                    '<button type="submit" data-time="' + data.type + '" data-date="' + data.date + '" data-staff="' + data.staffNumb + '" data-id="' + shiftId + '" onclick="regOvertime(this);" id="regOvertime" disabled>Registrer overtid</button></div>';
            }

        }

    }
    else if(data.shiftUsers.length < data.staffNumb){
        html+='<a href="availability.html?date='+data.date+'" ><button id="absence">Meld deg tilgjengelig</button></a>';
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
    console.log('register illness');
    prevDiv = that;
    var date = $(that).attr('data-date');
    var staff = $(that).attr('data-staff');
    console.log(date);
    console.log(staff);
    console.log(absenceIds);
    if(absenceIds.indexOf(shiftId)>-1) {
        return $(that).html('<div class="button-group"><button class="btn-secondary" disabled>Du har registrert sykdom</button></div>');
    }   
    displayPopup(shiftId,date,staff);
    function displayPopup(shiftId,date,staff) {
        $("#registerIllnessPopup p").replaceWith("Er du sikker på at du vil registrere sykdom for vakten din <b>" + convertDate(date) + "</b>?");
        $("#registerIllnessPopup").show();
    }
    //moreInfoElement.slideToggle();
}
function regOvertime(that) {
    var shiftId = $(that).attr("data-id");
    var date = $(that).attr("data-date");
    var type = $(that).attr("data-time");
    console.log(that);
    console.log($(that).attr("data-time"));
    url = "register-overtime.html?shiftId="+shiftId+"&date="+date+"&type="+type;
    window.location = url;
}
function poopStatus(status, that) {
    var shiftId = shiftIllness;
    if (status) {
        $.ajax({
            url: "../rest/shift/user/valid_absence/" + shiftId,
            type: 'GET',
            dataType: 'json',
            success: function(data) {
                absenceIds.push(shiftId);
                $(prevDiv).parent().replaceWith('<div class="button-group"><button class="btn-secondary" disabled>Du har registrert sykdom</button></div>');
            },
            error: function(data) {
                if(data.responseText == "Fristen for å melde sykdom har gått ut.") {
                    $('.dialogboks').html("<h3>Vennligst henvend deg til administrasjonen</h3>");
                    $("#popup").show();
                } else {
                     absenceIds.push(shiftId);
                     $(prevDiv).parent().replaceWith('<div class="button-group"><button class="btn-secondary" disabled>Du har registrert sykdom</button></div>');
                }
            }
        });
        $("#registerIllnessPopup").fadeOut();
    } else {
        $("#registerIllnessPopup").fadeOut();
    }
}

function closePopup() {
    $("#popup").fadeOut();
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
        $("#changeShiftPopup p").replaceWith("Er du sikker på at du vil registrere vaktbytte for vakten din <b>" + convertDate(date) + "</b>?");
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
        $(reqPrevDiv).parent().replaceWith('<div class="button-group"><button class="btn-secondary" disabled>Du har registrert vaktbytte</button></div>');
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
                "<p class='sub'><span class='circle blue'></span>Din vakt</p>" +
                "</div>";
        }
        else if (element.available) {
            html +=
                "<div class='watch-info'>" +
                "<p class='sub'><span class='circle green'></span>Ledig vakt</p>" +
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

