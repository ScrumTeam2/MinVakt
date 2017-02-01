/**
 * Created by olekristianaune on 27.01.2017.
 */

var $staffNumInput;
var $staffForm;
var $saveButton;
var $popup;
var shiftType;

var users = [];

var userTypes = {"ADMIN" : "Administrasjon", "ASSISTANT" : "Assistent", "HEALTH_WORKER" : "Helsefagarbeider", "NURSE" : "Sykepleier"};
var shiftTypes = {"DAY" : "Dagvakt", "EVENING" : "Kveldsvakt", "NIGHT" : "Nattevakt"};
var startTimes = {"DAY" : 420, "EVENING" : 900, "NIGHT" : 1380};

var $list = $('.list');

var shiftId = getUrlParameter("id");

$(document).ready(function() {
    $staffNumInput = $('#staffNum');
    $staffForm = $("#staffForm");
    $saveButton = $("#saveBtn");

    getNewShift();
});

function getNewShift() {
    $.ajax({
        url: "/rest/shift/" + shiftId,
        method: "GET",
        success: showShiftInfo,
        error: function(e) {
            console.error("Error", e);
        }
    });
}

function performSave(e) {
    e.preventDefault();
    var newStaffCount = $staffNumInput.val();

    // Set load animation
    $saveButton.html(`<div class="typing_loader"></div>`);

    $.ajax({
        url: "/rest/shift/" + shiftId + "/set_staff",
        method: "POST",
        data: {
            staffCount: newStaffCount
        }
    }).success(function (data) {
        //console.log(data);
        $saveButton.text("Lagre");
        getNewShift();
    }).error(function (e) {
        console.error(e);
        $popup.show();
        $saveButton.text("Lagre");
    });
}

function showShiftInfo(data) {
    //console.log(data);

    shiftType = data.type;
    $list.html("");

    $list.append(`<div class="container-title">
                <h3>${convertDate(data.date)} - ${shiftTypes[data.type]}</h3>
            </div>`);


    $saveButton.click(performSave);

    $staffNumInput.val(data.staffNumb);
    $staffForm.show();

    var counter = 0;
    while(counter < data.staffNumb) {
        if (counter < data.shiftUsers.length) {
            users.push(data.shiftUsers[counter]);
        } else {
            $list.append(`<div class="watch">
                    <div class="watch-info">
                        <p>Ledig</p>
                    </div>
                    <a href="/html/add-employee.html?shift=${data.id}" class="link">Legg til</a>
                </div>`);
        }
        counter++;
    }

    for(var i in users) {
        displayUsers(users[i]);
    }

    $('#btnConfirmAbsence').on('click', function() {
        $('#btnConfirmAbsence').html(`<div class="typing_loader"></div>`);
        var absence = [];
        var $employees = $('.watch');
        for (var i = 0; i < $employees.length; i++) {
            var $employee = $($employees[i]);
            var userId = $employee.attr('data-id');
            var shiftId = getUrlParameter('id');
            var hours = $employee.find('#hours').val();
            var minutes = 5 * Math.round($employee.find('#minutes').val()/5);;
            if (hours || minutes) {
                absence.push({
                    userId: userId,
                    shiftId: shiftId,
                    startTime: startTimes[shiftType],
                    minutes: -(hours*60 + minutes)
                });
            }
        }

        //console.log(absence.length);

        for (var i = 0; i < absence.length; i++) {
            $.ajax({
                url: "/rest/overtime/",
                type: "POST",
                contentType: 'application/json',
                data: JSON.stringify(absence[i]),
                success: function(data) {
                    $('#btnConfirmAbsence').html(`Registrer fravær`);
                    window.location = "/html/calendar-a.html";
                },
                error: function(e) {
                    $('#btnConfirmAbsence').html(`Registrer fravær`);
                    console.log("Error", e);
                    initPopup();
                    $("#userPopup").show();
                }
            });
        }

        if(absence.length === 0) { // TODO: Fix problem with popup
            initPopup();
            $("#userPopup").show();
        }
    });
}

function displayUsers(user) {
    $.ajax({
        url: "/rest/overtime/shiftId/" + getUrlParameter("id") + "?userId=" + user.userId,
        type: "GET",
        success: function(data) {
            console.log(data);
            var hours;
            var minutes;
            for (var i = 0; i < data.length; i++) {
                if (data[i].minutes < 0) {
                    var tempTime = data[i].minutes * -1;
                    hours = Math.floor(tempTime/60);
                    minutes = (tempTime - (hours * 60) === 0) ? '' : tempTime - (hours * 60);
                }
            }
            $list.append(`<div class="watch" data-id="${user.userId}">
                                    <div class="watch-info">
                                        <p class="lead">${user.userName}</p>
                                        <p class="sub">${userTypes[user.userCategory]}</p>
                                    </div>
                                    <div class="longer">
                                        <input type="number" id="hours" min="0" max="8" placeholder="Timer" value="${isNaN(hours) ? '' : hours}">
                                        <input type="number" id="minutes" min="0" max="55" step="5" placeholder="Minutter" value="${isNaN(minutes) ? '' : minutes}">
                                    </div>

                                </div>`);
        }
    });
}

function initPopup() {
    $popup = $("#userPopup");

    //close popup when clicking outside of the popup
    window.onclick = function(e) {
        if (!$popup.is(e.target) && $popup.has(e.target).length === 0) {
            $popup.hide();
        }
    };

    //close popup
    $('#userCloseBtn').click(function() {
        $popup.hide();
    });
}