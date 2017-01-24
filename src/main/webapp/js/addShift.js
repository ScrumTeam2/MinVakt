/**
 * Created by olekristianaune on 16.01.2017.
 */


$(document).ready(function() {
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
                console.log(x.name + " " + x.value);
                if(shiftNames.indexOf(x.name) > -1) {
                    shiftsEmployeeCount.push(parseInt(x.value));
                }
                a[x.name] = x.value;
                return a;
            }, {});

        console.log(data);
        console.log(shiftsEmployeeCount);

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

        console.log(days);
        console.log(JSON.stringify(days));


        var shiftPlan = {
            templateWeek: {
                days: days
            }
        };

        console.log(shiftPlan);

        $.ajax({
                url: "/rest/shiftplan/" + date,
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(shiftPlan)
        })
        .done(function(data) {
            console.log( "success", data );
            localStorage.setItem("TempShiftPlan", JSON.stringify(data));
            localStorage.setItem("TempShiftCurr", 0);
            window.location = "add-users-to-shift.html";
        })
        .fail(function(error) {
            console.log( "error", error );
        });
    });
});