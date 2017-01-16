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
        var shiftUsers = null;
        var deptId = parseInt(data.department);

        var day = {
            id: id,
            staffNumb: staffDay,
            date: date,
            type: 1,
            deptId: deptId,
            shiftUsers: shiftUsers,
        };

        console.log(JSON.stringify(day));
        console.log(day);

        $.ajax({
                url: "/rest/shift",
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                data: JSON.stringify(day)
            })
            .done(function() {
                console.log( "success" );
            })
            .fail(function(error) {
                console.log( "error", error );
            })
            .always(function() {
                console.log( "complete" );
            });


    });
});