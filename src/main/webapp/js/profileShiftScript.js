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
        url: "../rest/department",
        type: 'GET',
        dataType: 'json',
        data: {"withData": true},
        success: function (data) {
            var $dropdown = $("#dept-options");
            $.each(data, function (index, dept) {
                if(dept.deptId == sessionStorage.getItem("SessionIdDept")) {
                    $dropdown.append("<option selected name='category' id = '"+dept.deptId+"' value='" + dept.deptId + "'>" + dept.name +
                        "</option>");
                }
                else{
                    $dropdown.append("<option name='category' id = '"+dept.deptId+"' value='" + dept.deptId + "'>" + dept.name +
                        "</option>");
                }
                if(dept.hasAvailable){
                    $dropdown.children("#"+dept.deptId).append("<span class='circle green'></span>'")
                }
                if(dept.hasUser){
                    $dropdown.children("#"+dept.deptId).append("<span class='circle blue'></span>")
                }
            })
        },
        error: function (data) {
            //console.log("Error, no data found");
            var calendarList = $(".list");
            calendarList.append("<p>" + data + "</p>");
        }
    });


}
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
function addShiftInfoHtml (element, shiftId, data) {

    employeeCategories = {'ASSISTANT': 'Assistent', 'HEALTH_WORKER': 'Helsemedarbeider', 'NURSE': 'Sykepleier'};
    categoriesForLoop = ['ASSISTANT', 'HEALTH_WORKER', 'NURSE'];
    var shiftUsers = data.shiftUsers;
    var html = "";
    var iAmOnShift = false;
    var absence = 0;
    //Could be made more efficient
    var baseUrl = "../html/user-e.html?search=";
    console.log(data);
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
        });
    }
    //console.log(html);

    // Add shift change button + illness button
    if(iAmOnShift) {
        if(absence == 1 || absenceIds.indexOf(shiftId)>-1) {
            html +=
                /*'<div class="button-group"><button type="submit" onclick="regByttVakt();" id="regByttVakt">Bytt vakt</button><button type="submit" data-id="'+shiftId+'" onclick="regSykdom(this);" id="regSykdom">Du har registrert sykdom</button></div>';
                */
                '<div class="button-group"><button type="submit" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="regByttVakt();" id="regByttVakt">Bytt vakt</button>' +
                '<div class="dialogboks"><h3>Du har registrert sykdom</h3></div></div>';
                
        } else if(absence==2 || absenceIds.indexOf(shiftId)>-1) {
            html +=
                '<div class="button-group"><button type="submit" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="regByttVakt();" id="regByttVakt">Bytt vakt</button>' +
                '<div class="dialogboks"><h3>Ditt fravær for sykdom har blitt godkjent av betjening</h3></div></div>';
        }else {
            html +=
                '<div class="button-group"><button type="submit" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="regByttVakt();" id="regByttVakt">Bytt vakt</button>' +
                '<button type="submit" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="regSykdom(this);" id="regSykdom">Registrer sykdom</button>' +
                '<button type="submit" data-time="'+data.type+'" data-date="'+data.date+'" data-staff="'+data.staffNumb+'" data-id="'+shiftId+'" onclick="regOvertime(this);" id="regOvertime">Registrer overtid</button></div>';

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
function regOvertime(that) {
    var shiftId = $(that).attr("data-id");
    var date = $(that).attr("data-date");
    var type = $(that).attr("data-time");
    url = "register-overtime.html?shiftId="+shiftId+"&date="+date+"&type="+type;
    window.location = url;
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

