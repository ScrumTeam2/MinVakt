/**
 * Created by marith 18.01.2017.
 */

$(document).ready(function(){
    $('.watch').hide();
});

$('.right-arrow-circle').click(function() {
    $(this).parent().siblings('.watch').toggle('1000');
    $(this).toggleClass("rotate90");
});