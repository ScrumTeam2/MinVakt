/**
 * Created by marith 18.01.2017.
 */


$(document).ready(function(){
    $.ajax({
        url: "/rest/newsfeed",
        type: 'GET',
        success: showMessages,
        error: showEmpty
    });
});

$('.container-title').click(function() {
    var $this = $(this);
    $this.siblings('.watch').toggle('1000');
    $this.children('div').children('.right-arrow-circle').toggleClass("rotate90");
});


function showMessages(data){
    console.log("ok");
    //acceptChangeover();
    //acceptTimebank();
    //acceptAbsence();
}

//popups
$('#shift-popup').click(function(){
    var $popup = $('#accept-pop');

    var time = "tid";
    var department = "avdeling";

    $popup.html(
        `<h3>Godkjenne vaktbytte?</h3>
         <p>Tid: ${time}</p>
         <p>Avdeling: ${department}</p>
         <div class="links">
            <a href="#" id="userCloseBtn">Ja</a>
            <a href="#" id="acceptChangeBtn">Nei</a>
         </div>`
    );
});

$('#absence-popup').click(function(){
    var $popup = $('#accept-pop');

    var time = "tid";
    var department = "avdeling";

    $popup.html(
        `<h3>Godkjenne vaktbytte?</h3>
         <p>Tid: ${time}</p>
         <p>Avdeling: ${department}</p>
         <div class="links">
            <a href="#" id="userCloseBtn">Endre vakt</a>
            <a href="#" id="userViewBtn">Nei</a>
         </div>`
    );
});

$('#timebank-popup').click(function(){
    var $popup = $('#accept-pop');

    var name = "Navn";
    var period = "Periode";

    $popup.html(
        `<h3>Godkjenne timebank?</h3>
         <p>${name}</p>
         <p>Periode: ${period}</p>
         <div class="links">
            <a href="#" id="acceptTimeBtn">Ja</a>
            <a href="#" id="denyTimeBtn">Nei</a>
         </div>`
    );
});

$('#acceptTimeBtn').click(function(){

});

//no messages
function showEmpty(){
    console.log("no messages");
    var $list = $('.list');
    $list.append(
        `<div class="watch">
                <div class="watch-info">
                    <p class="lead">Ingen meldinger</p>
                </div>
            </div>`
    );
}

//Admin feed
function acceptChangeover(data){
    //console.log("Got requests for current user", data);

    var $change = $('#accept_change');
    var name1 = "Navn1";
    var name2 = "Navn2";
    var time = "Tid";
    var department = "Avdeling";
    var category1 = "Kategori1";
    var category2 = "Kategori2";

    for (var request in data.stuff) {
        $change.append(
            `<div class="watch" id="shift-popup">
                <div class="watch-info">
                    <p class="lead">${name1}, ${category1} -> ${name2}, ${category2}</p>
                    <p class="sub">${time}</p>
                    <p class="sub">${department}</p>
                </div>
                <i class="symbol right-arrow">
                    <i class="material-icons">chevron_right</i>
                </i>
            </div>`
        );
    }
}

function acceptAbsence(data){
    //console.log("Got requests for current user", data);

    var $absence = $('#accept_absence');
    var name = "Navn";
    var time = "Tid";
    var department = "Avdeling";

    for (var request in data.stuff) {
        $absence.append(
            `<div class="watch" id="absence-popup">
                <div class="watch-info">
                    <p class="lead">${name}</p>
                    <p class="sub">${time}</p>
                    <p class="sub">${department}</p>
                </div>
                <i class="symbol right-arrow">
                    <i class="material-icons">chevron_right</i>
                </i>
            </div>`
        );
    }
}

function acceptTimebank(data){

    var $timebank = $('#accept_timebank');
    var name = "Navn";
    var period = "Periode";

    for (var request in data.stuff) {
        $timebank.append(
            `<div class="watch" id="timebank-popup">
                <div class="watch-info">
                    <p class="lead">Godkjent overtid</p>
                    <p class="sub">${period}</p>
                </div>
                <i class="symbol right-arrow">
                    <i class="material-icons">chevron_right</i>
                </i>
            </div>`
        );
    }
}
