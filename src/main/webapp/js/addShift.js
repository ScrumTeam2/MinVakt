/**
 * Created by olekristianaune on 16.01.2017.
 */

var $departmentSelect;

$(document).ready(function() {
    loadDepartments();
    var dayCodes = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
    var shiftTypeCodes = ["DAY", "EVENING", "NIGHT"];
    generateDaysHTML();
    function generateDaysHTML() {
        var days = ["Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag", "Søndag"];

        var $container = $("#days-placeholder");

        days.forEach(function(day, index) {
            $container.append(`
            <div class="container-day" data-day-code="${dayCodes[index]}">
                <div class="container-title">
                    <h3>${day}</h3>
                </div>
                <div class="num-employees">
                    <label for="day">Dag</label>
                    <input type="number" name="day" id="day" placeholder="Antall ansatte" />
                    <label for="evening">Kveld</label>
                    <input type="number" name="evening" id="evening" placeholder="Antall ansatte" />
                    <label for="night">Natt</label>
                    <input type="number" name="night" id="night" placeholder="Antall ansatte" />
                </div>
            </div>`);
        });
    }

    $('#createShiftBtn').on("click", function(e) {
        e.preventDefault();

        $('#createShiftBtn').html(
            `<div class="typing_loader"></div>`
        );

        var shiftNames = ["day", "evening", "night"];
        var shiftsEmployeeCount = [ ];

        var data = $('#shiftForm').serializeArray()
            .reduce(function(a, x) {
                if(shiftNames.indexOf(x.name) > -1) {
                    shiftsEmployeeCount.push(parseInt(x.value));
                }
                a[x.name] = x.value;
                return a;
            }, {});

        var id = -1;
        var date = data.date;
        var shiftUsers = [];
        var deptId = parseInt(data.department);
        var days = [ ];

        // Counter variable
        var shiftCounter = 0;

        // Seven days
        for(var i = 0; i < 7; i++) {
            var day = {
                dayOfWeek: dayCodes[i],
                shifts: [ ]
            };

            for(var j = 0; j < 3; j++) {
                var shift = {};
                shift.shift = {
                    staffNumb: shiftsEmployeeCount[shiftCounter++],
                    deptId: deptId,
                    type: shiftTypeCodes[j]
                };
                day.shifts.push(shift);
            }
            days.push(day);
        }

        var shiftPlan = {
            departmentId: deptId,
            templateWeek: {
                days: days
            }
        };

        $.ajax({
            url: "/rest/shiftplan/" + date,
            type: "POST",
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(shiftPlan),
            success: function(data) {
                localStorage.setItem("TempShiftPlan", JSON.stringify(data));
                localStorage.setItem("TempShiftCurr", 0);
                window.location = "add-users-to-shift.html";
            },
            error: invalidField
        });
    });

    //close popup when clicking outside of the popup
    var $popup = $('#userPopup');
    window.onclick = function(e) {
        if (!$popup.is(e.target) && $popup.has(e.target).length === 0) {
            $popup.hide();
        }
    };
});

function loadDepartments() {
    $departmentSelect = $("#department");

    $.get("/rest/department/withData")
        .done(function (data) {
            data.forEach(function (department) {
                $departmentSelect.append(`<option value="${department.id}">${department.name}</option>`);
            })
        })
        .fail(function (data) {
            console.log("fail", data);
        });
}

function invalidField(data){
    $('.title').text("Feil");

    if(data.responseJSON == null) {
        $('.result').text("En uventet feil oppsto");
    } else {
        $('.result').text(data.responseJSON.error);
    }

    $('#userViewBtn').hide();
    $('.popup').show();

    // Remove loading animation
    $('#createShiftBtn').text("Generer turnus");
}

//close popup
$('#userCloseBtn').click(function() {
    $('.popup').hide();
});
