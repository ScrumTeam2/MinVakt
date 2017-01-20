/**
 * Created by ingvildbroen on 20.01.2017.
 */

$(document).ready(function(){
    $.ajax({
        url: "/rest/user/",
        type: 'GET',
        success: showMessages
    });
});

$('.container-title').click(function() {
    var $this = $(this);
    $this.siblings('.watch').toggle('1000');
    $this.children('div').children('.right-arrow-circle').toggleClass("rotate90");
});


$('#open-popup').click(function(){

});

/*function showMessages(data){
    console.log("Got messages for current user", data);

    //forespørsel om vaktbytte
    var $requests = $('#requests');
    html =
        "<div class='watch' id='open-popup'>"+
        "<div class='watch-info'>" +
        "<p class='lead'>Navn</p>" +
        "<p class='sub'>Tid</p>" +
        "<p class='sub'>Avdeling</p>" +
        "</div>" + "</div>" +
        "<i class='symbol right-arrow'>"+
        "<i class='material-icons'>chevron_right</i></i>"
        + user.phoneNumber + "</i>" +
        "<p class='more-info__text'>100% stilling</p>";


    $requests.html("");
    for (var request in data.stuff) {
        $requests.append(
            '<div class="watch" id="open-popup">Sykkel nr. </div>' +
            '<div class="watch" tabindex="0">' +
            '<div class="bike__info">' +
            '<h3>' + data.availableBikes[bike].id + '</h3>' +
            '<div class="progress" aria-valuenow="' + (data.availableBikes[bike].battery * 100) + '">' +
            '<div class="progress-bar progress-bar-' + (((data.availableBikes[bike].battery * 100) > 50) ? 'success' : 'danger') + '" aria-valuenow="' + (data.availableBikes[bike].battery * 100) + '" aria-valuemin="0" aria-valuemax="100" style="width:' + (data.availableBikes[bike].battery * 100) + '%">  </div>' +
            '</div>' +
            '</div>' +
            '</div>'
        );
    }

    //godkjent vaktbytte

    //godkjent timebank

}
*/