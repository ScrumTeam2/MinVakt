/**
 * Created by Audun on 25.01.2017.
 */

var $staffNumInput;
var $staffForm;
var $saveButton;
var $popup;

var userTypes = {"ADMIN" : "Administrasjon", "ASSISTANT" : "Assistent", "HEALTH_WORKER" : "Helsemedarbeider", "NURSE" : "Sykepleier"};
var shiftTypes = {"DAY" : "Dagvakt", "EVENING" : "Kveldsvakt", "NIGHT" : "Nattevakt"};

var $list = $('.list');

var shiftId = getUrlParameter("id");

$(document).ready(function() {
    $staffNumInput = $('#staffNum');
    $staffForm = $("#staffForm");
    $saveButton = $("#saveBtn");

    initPopup();
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
        console.log(data);
        $saveButton.text("Lagre");
        getNewShift();
    }).error(function (data) {
        console.log(data);
        $popup.show();
        $saveButton.text("Lagre");
    });
}

function showShiftInfo(data) {
    console.log(data);

    var output = `<div class="container-title">
                <h3>${convertDate(data.date)} - ${shiftTypes[data.type]}</h3>
            </div>`;


    $saveButton.click(performSave);

    $staffNumInput.val(data.staffNumb);
    $staffForm.show();

    var counter = 0;
    while(counter < data.staffNumb) {
        if (counter < data.shiftUsers.length) {
            var user = data.shiftUsers[counter];
            output += `<div class="watch">
                    <div class="watch-info">
                        <p class="lead">${user.userName}</p>
                        <p class="sub">${userTypes[user.userCategory]}</p>
                    </div>
                    <div>
                    <a href="/html/change-employee.html?user=${user.userId}&shift=${data.id}&edit=1" class="link">Bytt</a>
                    <a href="#" data-userId="${user.userId}" class="link btnRemove">Fjern</a>
                    </div>

                </div>`;
        } else {
            output += `<div class="watch">
                    <div class="watch-info">
                        <p>Ledig</p>
                    </div>
                    <a href="/html/add-employee.html?shift=${data.id}" class="link">Legg til</a>
                </div>`;
        }
        counter++;
    }

    $list.html(output);

    $('.btnRemove').on('click', function(e) {
        var userId = $(e.currentTarget).attr("data-userId");
        $.ajax({
            url: "/rest/shift/" + getUrlParameter("id") + "/user/" + userId + "?findNewEmployee=false",
            type: "DELETE",
            success: getNewShift,
            error: function(e) {
                console.error("Problem deleting user", e);
            }
        });
    });
}

function initPopup() {
    $popup = $("#userPopup");

    //close popup when clicking outside of the popup
    window.onclick = function(event) {
        if (event.target != $popup) {
            $popup.hide();
        }
    };

    //close popup
    $('#userCloseBtn').click(function() {
        $popup.hide();
    });
}