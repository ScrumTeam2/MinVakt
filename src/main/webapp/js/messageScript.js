/**
 * Created by ingvildbroen on 20.01.2017.
 */

$(document).ready(function(){
    $.ajax({
        url: "/rest/newsfeed/",
        type: 'GET',
        success: showMessages
    });
});

$('.container-title').click(function() {
    var $this = $(this);
    $this.siblings('.watch').toggle('1000');
    $this.children('div').children('.right-arrow-circle').toggleClass("rotate90");
});


function showMessages(data){
    showChangeover();
    showTimebank();
    showRequest();
}


$('#open-popup').click(function(){
    var $popup = $('#accept-pop');

    var time = "tid";
    var department = "avdeling";

    $popup.html(
        `<h3>Godkjenne vakt?</h3>
         <p>Tid: ${time}</p>
         <p>Avdeling: ${department}</p>
         <div class="links">
            <a href="#" id="userCloseBtn">Ja</a>
            <a href="#" id="userViewBtn">Nei</a>
         </div>`
    );
});

function showEmpty(){
    console.log("no messages");
}


//employee feed

//Requests for a new shift
function showRequest(data){
    //console.log("Got requests for current user", data);

    var $requests = $('#requests');
    var name = "Navn";
    var time = "Tid";
    var department = "Avdeling";

    for (var request in data.stuff) {
        $requests.append(
            `<div class="watch" id="open-popup">
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

//Solved/unsolved changeover
function showChangeover(data){
    console.log("Got changeover for current user", data);

    var $changeovers = $('#changeovers');
    var status = "Godkjent/ikke godkjent";
    var time = "Tid";

    for (var changeover in data.stuff) {
        $changeovers.append(
            `<div class="watch" id="remove">
                <div class="watch-info">
                    <p class="lead">${status}</p>
                    <p class="sub">${time}</p>
                </div>
                <i class="material-icons">close</i>
            </div>`
        );
    }
}

//Accepted timebank
function showTimebank(data){
    console.log("Got timebank for current user", data);

    var $timebanks = $('#timebanks');
    var period = "Periode";

    for (var timebank in data.stuff) {
        $timebanks.append(
            `<div class="watch" id="remove">
                <div class="watch-info">
                    <p class="lead">Godkjent overtid</p>
                    <p class="sub">${period}</p>
                </div>
                <i class="material-icons">close</i>
            </div>`
        );
    }
}