/**
 * Created by olekristianaune on 17.01.2017.
 */

var shiftPlan = JSON.parse(localStorage.getItem("TempShiftPlan"));
var currentNum = parseInt(localStorage.getItem("TempShiftCurr"));
console.log("shiftPlan", shiftPlan);
console.log("currentNum", currentNum);

var userTypes = {"ADMIN" : "Administrasjon", "ASSISTANT" : "Assistent", "HEALTH_WORKER" : "Helsemedarbeider", "NURSE" : "Sykepleier"};
var shiftTypes = {"DAY" : "Dagvakt", "EVENING" : "Kveldsvakt", "NIGHT" : "Nattevakt"};

var $list = $('.list');

$(document).ready(function() {

    $('.btnNext').on("click", function(e) {
        currentNum = parseInt(currentNum) + 1;
        $('.btnPrev').show();
        if (currentNum >= shiftPlan.length) {
            console.log(JSON.stringify(shiftPlan));
            $.ajax({
                url: "/rest/shiftplan/approve",
                type: 'POST',
                contentType: "application/json",
                data: JSON.stringify({
                    content: shiftPlan
                }),
                success: function(data) {
                    console.log(data);
                    //TODO: Make some popup to confirm that the shifts were approved.
                    localStorage.clear();
                    window.location = "/html/new-shift.html";
                },
                error: function(e) {
                    console.error("Error", e);
                }
            });

        } else {
            if (currentNum === shiftPlan.length - 1) {
                $('.btnNext').text("Publiser");
            }
            localStorage.setItem("TempShiftCurr", currentNum);
            getNewShift();
        }
    });

    $('.btnPrev').on("click", function(e) {
        currentNum = Math.max(0, currentNum - 1);
        if (currentNum === 0) {
            $('.btnPrev').hide();
        }
        if (currentNum !== shiftPlan.length - 1) {
            $('.btnNext').text("Neste");
        }
        localStorage.setItem("TempShiftCurr", currentNum);
        getNewShift();
    });

    if (currentNum === 0) {
        $('.btnPrev').hide();
    }
    if (currentNum === shiftPlan.length - 1) {
        $('.btnNext').text("Publiser");
    }
    getNewShift();
});

function getNewShift() {
    $.ajax({
        url: "/rest/shift/" + shiftPlan[currentNum],
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
                <p>${currentNum + 1} av ${shiftPlan.length}</p>
            </div>`;

    for (var i in data.shiftUsers) {
        var user = data.shiftUsers[i];
        output += `<div class="watch">
                    <div class="watch-info">
                        <p class="lead">${user.userName}</p>
                        <p class="sub">${userTypes[user.userCategory]}</p>
                    </div>
                    <a href="/html/change-employee.html?user=${user.userId}&shift=${data.id}" class="link">Endre</a>
                </div>`;
    }

    $list.html(output);
}