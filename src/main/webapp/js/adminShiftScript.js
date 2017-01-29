/**
 * Created by evend on 1/12/2017.
 */
$(document).ready(function () {
    createAjaxForAllShifts();
    createCalendarListener();
    setDeptOptions();
});
//});

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

function addShiftInfoHtml (element, shiftId, data) {

    employeeCategories = {'ASSISTANT': 'Assistent', 'HEALTH_WORKER': 'Helsefagarbeider', 'NURSE': 'Sykepleier'};
    categoriesForLoop = ['ASSISTANT', 'HEALTH_WORKER', 'NURSE'];
    var shiftUsers = data.shiftUsers;
    var html = "";

    //Could be made more efficient
    var baseUrl = "../html/user-e.html?search=";
    for (var i = 0; i < categoriesForLoop.length; i++) {
        var hasPerson = false;
        //console.log(categoriesForLoop[i]);
        //console.log(html);
        $.each(shiftUsers, function (index, user) {
            if (user.userCategory == categoriesForLoop[i]) {
                if (!hasPerson) {
                    html += "<h4>" + employeeCategories[categoriesForLoop[i]] + "</h4>";
                    hasPerson = true;
                }
                if (user.responsibility) {
                    html += "<a href='" + baseUrl + user.userName +
                        "' class='link'>" + user.userName + " (Vaktansvarlig)<i class='material-icons'>chevron_right</i></a>"
                }
                else {
                    html += "<a href='" + baseUrl + user.userName + "' class='link'>" + user.userName + "<i class='material-icons'>chevron_right</i></a>"
                }
            }
        });
    }
    //console.log(html);

    element.append(html);
    // Add edit button
    element.append(`<div class="button-group">
                        <button data-id="${shiftId}" id="editBtn">Endre vakt</button>
                        <button data-id="${shiftId}" id="absenceBtn">Registrer fravær</button>
                    </div>`);

    $('#editBtn').on('click', function(e) {
        e.preventDefault();
        var $this = $(e.currentTarget);
        window.location = "/html/edit-shift.html?id=" + $this.attr("data-id");
    });

    $('#absenceBtn').on('click', function(e) {
        e.preventDefault();
        var $this = $(e.currentTarget);
        window.location = "/html/register-absence.html?id=" + $this.attr("data-id");
    });
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
        if (element.available) {
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

