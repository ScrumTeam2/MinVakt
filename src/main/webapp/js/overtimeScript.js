/**
 * Created by AnitaKristineAune on 24.01.2017.
 */

var shiftId = getUrlParameter("shiftId");
var content = getUrlParameter("message");

$(document).ready(function () {
    getShift();
});


function sendTimebank(){

}

function getShift(){

}

function registerOvertime(){

}

function registerAbsence(){

}

function showPopup(){
    var $popup = $('.popup');
    $popup.show();

}

function hidePopup(){
    var $popup = $('.popup');
    $popup.hide();

}