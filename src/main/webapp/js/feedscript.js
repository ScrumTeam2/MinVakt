/**
 * Created by ingvildbroen on 16.01.2017.
 */

$('#showStuff').click(function() {
    $('#display_watch').toggle('1000');
    $("i", '#showStuff').toggleClass("rotate90 right-arrow-circle");
    //var $display = $('.watch');
    //$display.removeClass("hide");
});