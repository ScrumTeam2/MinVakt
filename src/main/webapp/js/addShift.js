/**
 * Created by olekristianaune on 16.01.2017.
 */


$(document).ready(function() {
    $('#createShiftBtn').on("click", function(e) {
        e.preventDefault();

        var data = $('#shiftForm').serializeArray()
            .reduce(function(a, x) { a[x.name] = x.value; return a; }, {});

        var id = -1;
        var staffDay = parseInt(data.day);
        var staffEvening = parseInt(data.evening);
        var staffNight = parseInt(data.night);
        var date = data.date;
        var shiftUsers = [];
        var deptId = parseInt(data.department);

        var day = {
            id: id,
            staffNumb: staffDay,
            date: date,
            type: "DAY",
            deptId: deptId,
            shiftUsers: shiftUsers,
        };

        var evening = {
            id: id,
            staffNumb: staffEvening,
            date: date,
            type: "EVENING",
            deptId: deptId,
            shiftUsers: shiftUsers,
        };

        var night = {
            id: id,
            staffNumb: staffNight,
            date: date,
            type: "NIGHT",
            deptId: deptId,
            shiftUsers: shiftUsers,
        };

        $.ajax({
                url: "/rest/shift",
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(day)
        })
        .done(function(data) {
            console.log( "success", data );
            localStorage.setItem("TempDayId", data.id);
            createEvening();
        })
        .fail(function(error) {
            console.log( "error", error );
        });

        function createEvening() {
            $.ajax({
                    url: "/rest/shift",
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json",
                    data: JSON.stringify(evening)
                })
                .done(function(data) {
                    console.log( "success", data );
                    localStorage.setItem("TempEveningId", data.id);
                    createNight()
                })
                .fail(function(error) {
                    console.log( "error", error );
                });
        }

        function createNight() {
            $.ajax({
                    url: "/rest/shift",
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json",
                    data: JSON.stringify(night)
                })
                .done(function(data) {
                    console.log( "success", data );
                    localStorage.setItem("TempNightId", data.id);
                    window.location = "add-users-to-shift.html";
                })
                .fail(function(error) {
                    console.log( "error", error );
                });
        }

    });
});