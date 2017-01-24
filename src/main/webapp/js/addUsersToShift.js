/**
 * Created by olekristianaune on 17.01.2017.
 */

var shiftPlan = JSON.parse(localStorage.getItem("TempShiftPlan"));
console.log("Retrieve", shiftPlan);
var currentNum = localStorage.getItem("TempShiftCurr");
var currentWeek = Math.floor(currentNum / 7);
var currentDay = currentNum % 7;

var currentObj = shiftPlan.generatedWeeks[currentWeek].days[currentDay];

var shiftTypes = {"ADMIN" : "Administrasjon", "ASSISTANT" : "Assistent", "HEALTH_WORKER" : "Helsemedarbeider", "NURSE" : "Sykepleier"};

var $list = $('.list');

$(document).ready(function() {

    $('button').on("click", function(e) {
        localStorage.setItem("TempShiftCurr", currentNum + 1);


        // TODO: Mark shift as published with the ids, stored in localStorage
        //localStorage.clear();
        //window.location = "/html/new-shift.html";
    });

    var shifts = currentObj.shifts;
    for (var i in shifts) {
        var currShift = shifts[i].shift;
        var output = `<div class="container-title">
                    <h3>${currShift.date} ${currShift.type}</h3>
                </div>`;

        for (var j in currShift.shiftUsers) {
            var user = currShift.shiftUsers[j];
            output += `<div class="watch">
                        <div class="watch-info">
                            <p class="lead">${user.userName}</p>
                            <p class="sub">${shiftTypes[user.userCategory]}</p>
                        </div>
                        <a href="#" data-userId="${user.userId}" data-shiftId="${currShift.id}" class="link changeUser">Endre</a>
                    </div>`;
        }

        $list.append(output);
    }



});