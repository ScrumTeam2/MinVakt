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


function showMessages(data){
    console.log("Got messages for current user", data);

}