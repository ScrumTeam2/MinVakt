/**
 * Created by olekristianaune on 17.01.2017.
 */

var dayId = localStorage.getItem("TempDayId");
var eveningId = localStorage.getItem("TempEveningId");
var nightId = localStorage.getItem("TempNightId");

var shiftTypes = {"ADMIN" : "Administrasjon", "ASSISTANT" : "Assistent", "HEALTH_WORKER" : "Helsemedarbeider", "NURSE" : "Sykepleier"};

var $list = $('.list');

$(document).ready(function() {

    $('button').on("click", function(e) {
        // TODO: Mark shift as published with the ids, stored in localStorage
    });

    $.ajax({
        url: "/rest/shift/" + dayId + "/possiblecandidates",
        type: "GET"
    })
    .done(function(data) {
        console.log( "success", data );

        var output = `<div class="container-title">
                        <h3>Dag</h3>
                    </div>`;


        for (var i in data) {
            output += `<div class="watch">
                            <div class="watch-info">
                                <p class="lead">${data[i].firstName} ${data[i].lastName}</p>
                                <p class="sub">${shiftTypes[data[i].category]}</p>
                            </div>
                            <a href="#" data-id="${data[i].id}" class="link">Endre</a>
                        </div>`;
        }

        $list.append(output);
        getEvening();
    })
    .fail(function(error) {
        console.log( "error", error );
    });
});

function getEvening() {
    $.ajax({
            url: "/rest/shift/" + eveningId + "/possiblecandidates",
            type: "GET"
        })
        .done(function(data) {
            console.log( "success", data );

            var output = `<div class="container-title">
                        <h3>Kveld</h3>
                    </div>`;


            for (var i in data) {
                output += `<div class="watch">
                            <div class="watch-info">
                                <p class="lead">${data[i].firstName} ${data[i].lastName}</p>
                                <p class="sub">${shiftTypes[data[i].category]}</p>
                            </div>
                            <a href="#" data-id="${data[i].id}" class="link">Endre</a>
                        </div>`;
            }

            $list.append(output);
            getNight();
        })
        .fail(function(error) {
            console.log( "error", error );
        });
}

function getNight() {
    $.ajax({
            url: "/rest/shift/" + nightId + "/possiblecandidates",
            type: "GET"
        })
        .done(function(data) {
            console.log( "success", data );

            var output = `<div class="container-title">
                        <h3>Natt</h3>
                    </div>`;


            for (var i in data) {
                output += `<div class="watch">
                            <div class="watch-info">
                                <p class="lead">${data[i].firstName} ${data[i].lastName}</p>
                                <p class="sub">${shiftTypes[data[i].category]}</p>
                            </div>
                            <a href="#" data-id="${data[i].id}" class="link">Endre</a>
                        </div>`;
            }

            $list.append(output);
        })
        .fail(function(error) {
            console.log( "error", error );
        });
};