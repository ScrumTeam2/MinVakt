/**
 * Created by marith 18.01.2017.
 */


$(document).ready(function(){
    //$('.watch').hide();
    $('.right-arrow-circle').toggleClass("rotate90");
});

$('.container-title').click(function() {
    var $this = $(this);
    $this.siblings('.watch').toggle('1000');
    $this.children('div').children('.right-arrow-circle').toggleClass("rotate90");
});


