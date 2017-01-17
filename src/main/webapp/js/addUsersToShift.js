/**
 * Created by olekristianaune on 17.01.2017.
 */

var dayId = localStorage.getItem("TempDayId");
var eveningId = localStorage.getItem("TempEveningId");
var nightId = localStorage.getItem("TempNightId");


var $list = $('.list');

$(document).ready(function() {
    $.ajax({
        url: "/rest/shift",
        type: "GET",
        data: {
            dataType: "json"
        }
    })
    .done(function(data) {
        console.log( "success", data );

        var output = `<div class="container-title">
                        <h3>Dag</h3>
                    </div>`;


        for (var i in data) {
            output += `<div class="watch">
                            <div class="watch-info">
                                <p class="lead">{data[i].firstName} {data[i].lastName}</p>
                                <p class="sub">{data[i].category}</p>
                            </div>
                            <a href="#" data-id="{data[i].id}" class="link">Endre</a>
                        </div>`;
        }

        $list.append(output);
    })
    .fail(function(error) {
        console.log( "error", error );
    });
});

$(document).ready(function() {
    $.ajax({
            url: "/rest/shift",
            type: "GET",
            data: {
                dataType: "json"
            }
        })
        .done(function(data) {
            console.log( "success", data );

            var output = `<div class="container-title">
                        <h3>Kveld</h3>
                    </div>`;


            for (var i in data) {
                output += `<div class="watch">
                            <div class="watch-info">
                                <p class="lead">{data[i].firstName} {data[i].lastName}</p>
                                <p class="sub">{data[i].category}</p>
                            </div>
                            <a href="#" data-id="{data[i].id}" class="link">Endre</a>
                        </div>`;
            }

            $list.append(output);
        })
        .fail(function(error) {
            console.log( "error", error );
        });
});

$(document).ready(function() {
    $.ajax({
            url: "/rest/shift",
            type: "GET",
            data: {
                dataType: "json"
            }
        })
        .done(function(data) {
            console.log( "success", data );

            var output = `<div class="container-title">
                        <h3>Natt</h3>
                    </div>`;


            for (var i in data) {
                output += `<div class="watch">
                            <div class="watch-info">
                                <p class="lead">{data[i].firstName} {data[i].lastName}</p>
                                <p class="sub">{data[i].category}</p>
                            </div>
                            <a href="#" data-id="{data[i].id}" class="link">Endre</a>
                        </div>`;
            }

            $list.append(output);
        })
        .fail(function(error) {
            console.log( "error", error );
        });
});