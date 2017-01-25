/**
 * Created by Audun on 25.01.2017.
 */

var userTypes = {"ADMIN" : "Administrasjon", "ASSISTANT" : "Assistent", "HEALTH_WORKER" : "Helsemedarbeider", "NURSE" : "Sykepleier"};
var shiftTypes = {"DAY" : "Dagvakt", "EVENING" : "Kveldsvakt", "NIGHT" : "Nattevakt"};

var $list = $('.list');

var shiftId = getUrlParameter("id");

$(document).ready(function() {
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

function showShiftInfo(data) {
    console.log(data);

    var output = `<div class="container-title">
                <h3>${convertDate(data.date)} - ${shiftTypes[data.type]}</h3>
            </div>`;

    for (var i in data.shiftUsers) {
        var user = data.shiftUsers[i];
        output += `<div class="watch">
                    <div class="watch-info">
                        <p class="lead">${user.userName}</p>
                        <p class="sub">${userTypes[user.userCategory]}</p>
                    </div>
                    <a href="/html/change-employee.html?user=${user.userId}&shift=${data.id}&edit=1" class="link">Endre</a>
                </div>`;
    }

    $list.html(output);
}