/**
 * Created by Audun on 25.01.2017.
 */

var $staffNumInput;
var $staffForm;
var $saveButton;
var $cancelButton;
var $popup;

var userTypes = {"ADMIN" : "Administrasjon", "ASSISTANT" : "Assistent", "HEALTH_WORKER" : "Helsemedarbeider", "NURSE" : "Sykepleier"};
var shiftTypes = {"DAY" : "Dagvakt", "EVENING" : "Kveldsvakt", "NIGHT" : "Nattevakt"};

var $list = $('.list');

var shiftId = getUrlParameter("id");
var feedId = getUrlParameter("feedId");
var userId = getUrlParameter("userId");
feedId = feedId && feedId > 0 ? feedId : 0;
userId = userId && userId > 0 ? userId : 0;

$(document).ready(function() {
    $staffNumInput = $('#staffNum');
    $staffForm = $("#staffForm");
    $saveButton = $("#saveBtn");
    $cancelButton = $("#cancel-button");
    console.log($cancelButton);
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
        var userRemoved = true;
        var elements = $(".watch");
        for(var i = 0;i<elements.length;i++){
            if(($(elements[i]).attr("data-userId")) == userId){
                userRemoved = false;
            }
        }
        if(userRemoved){
            $.ajax({
                url: "/rest/newsfeed/" + feedId,
                method: "POST",
                data: {"accepted" : true, "alreadyEdited" : true},
                success: function() {
                    window.location = getUrlParameter("feedId") ? "home-a.html" : "user-a.html";
                },
                error: function(e) {
                    console.error("Error", e);
                }
            });
        }

    }).error(function (data) {
        console.log(data);
        $popup.show();
        $saveButton.text("Lagre");
    });
}

function showShiftInfo(data) {
    var employeesPerCategory = { "ADMIN": 0, "ASSISTANT": 0, "HEALTH_WORKER": 0, "NURSE": 0 };

    var output = `<div class="container-title">
                <h3>${convertDate(data.date)} - ${shiftTypes[data.type]}</h3>
                </div>`;

    //Add cancel button
    if(feedId > 0 && userId > 0){
        $cancelButton.removeClass("hide");
        $cancelButton.click(function(){
            console.log("Dette var feil");
            window.location = "home-a.html"
        });
    }
    $saveButton.click(performSave);
    $staffNumInput.val(data.staffNumb);
    $staffForm.show();

    var counter = 0;

    while(counter < data.staffNumb) {
        if (counter < data.shiftUsers.length) {
            var user = data.shiftUsers[counter];
            if(feedId && feedId > 0 && userId == user.userId){
                output += "<div class='watch highlight' data-userId='"+user.userId+"' data-category='"+user.userCategory+"'>";
            }
            else{
                output += '<div class="watch" data-category="'+user.userCategory+'">';
            }
            output += `<div class="watch-info">
                        <p class="lead">${user.userName}</p>
                        <p class="sub">${userTypes[user.userCategory]}</p>
                    </div>
                    <div>
                    <a href="/html/change-employee.html?user=${user.userId}&shift=${data.id}&edit=1&feedId=${feedId}&userId=${userId}" class="link">Bytt</a>
                    <a href="#" data-userId="${user.userId}" class="link btnRemove">Fjern</a>
                    </div>
                </div>`;
        } else {
            output += `<div class="watch">
                    <div class="watch-info">
                        <p>Ledig</p>
                    </div>
                    <a href="/html/add-employee.html?shift=${data.id}&feedId=${feedId}&userId=${userId}" class="link">Legg til</a>
                </div>`;
        }
        counter++;
    }
    console.log(employeesPerCategory);

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
    var percentages = updateCategoryRatio();
    createStatusHtml(percentages);
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
function updateCategoryRatio() {
    var staffCount = $staffNumInput.val();
    var assistantCount = 0;
    var healthWorkerCount = 0;
    var nurseCount = 0;
    var elements = $(".watch");
    for(var i = 0;i<elements.length;i++){
        switch ($(elements[i]).attr("data-category")){
            case 'ASSISTANT':
                assistantCount++;
                break;
            case 'NURSE':
                nurseCount++;
                break;
            case 'HEALTH_WORKER':
                healthWorkerCount++;
                break;
        }
    }

    //Assistant percentage, Nurse percentage, Health Worker percentage
    return [assistantCount / staffCount,
            nurseCount / staffCount,
        healthWorkerCount / staffCount];
}
function createStatusHtml(percentages) {
    var $divElement = $("#shift-status");
    $divElement.text("");
    var html = "";
    console.log(percentages);

    //Assistants
    html += "<p class=''>Assistenter: "+Math.round(percentages[0]*100)+"%</p>";

    //Nurses
    if(percentages[1] < 0.20){
        html += "<p class='red'>Sykepleiere: "+Math.round(percentages[1]*100)+"%</p>";
    }
    else{
        html += "<p class=''>Sykepleiere: "+Math.round(percentages[1]*100)+"%</p>";
    }
    //Health workers
    if(percentages[2] < 0.30){
        html += "<p class='red'>Helsefagarbeidere: "+Math.round(percentages[2]*100)+"%</p>";
    }
    else {
        html += "<p class=''>Helsefagarbeidere: " + Math.round(percentages[2] * 100) + "%</p>";
    }
    $divElement.append(html);
}

