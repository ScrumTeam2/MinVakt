/**
 * Created by ingvildbroen on 12.01.2017.
 */

$(document).ready(function(){

    var currPage = window.location.pathname.replace("/html/", "");
    var $menu = $('#popup_menu');
    var test = $menu.find($("a"));

    for (var i in test) {
        var $link = $(test[i]);
        if($link.attr("href") === currPage) {
            $link.addClass("active");
        }
    }


        //.addClass("active");

    if(sessionStorage.getItem("SessionId")){
        var expire = sessionStorage.getItem("SessionExpires");
        var date = new Date();
        var timeNow = date.getTime();

        var userCategory = sessionStorage.getItem("SessionIdCat");
        if(userCategory === 'ADMIN'){
            console.log("admin");
            adminAccess();
        } else if(userCategory === 'HEALTH_WORKER' || userCategory === 'ASSISTANT' || userCategory === 'NURSE'){
            console.log("assistant, nurse or health");
            console.log("path ", window.location.pathname);
            employeeAccess();
        } else{
            console.log("category not found", userCategory);
            redirect();
        }

        if(expire <= timeNow){
            $.ajax({
                url: "/rest/session/check",
                type: 'GET',
                success: renewSession,
                error: redirect
            });
        }
    } else{
        redirect();
    }
});



//Menu button
$('#menu_icon').click(function(e) {
    e.preventDefault();
    var $menu = $('#popup_menu');
    $menu.toggle();
    var $more = $('#popup_more');
    if ($more.is(":visible")) {
        $more.hide();
    }
});

//More button
$('#more_icon').click(function(e) {
    e.preventDefault();
    var $more = $('#popup_more');
    $more.toggle();
    var $menu = $('#popup_menu');
    if ($menu.is(":visible")) {
        $menu.hide();
    }
});

$('#logout').click(function(e){
    e.preventDefault();
    logOut();
});


function logOut(){
    $.ajax({
        url: "/rest/session/log_out",
        type: 'GET',
        success: redirect
    });
    //console.log("log out function");
}


function renewSession(){
    //console.log("renew");
    var date = new Date();
    date.setTime(date.getTime() + (2 * 60 * 60 * 1000));
    sessionStorage.SessionExpires = date;
}

function redirect(){
    //console.log("log out");
    //console.log("login again");
    sessionStorage.clear();
    window.location = "login.html";
}

/*
* Ansatt:
* Ansattinfo (begrenset)                -- user-e.html
* Bytte vakt                            --
* Fravær/sykdom                         --
* Registrere tilgjengelighet            -- availability.html
* Registrere overtid i timebank         --
* Egne vakter                           -- home-e.html
*
* Admin:
* Registrer bruker                      -- register.html
* Ansattinfo (alt)                      -- user-a.html
* Tilgjengelighet for hver ansatt       --
* Rediger vakt (ansvarsvakt og ansatte) --
* Godkjenne vaktbytte (feed og vanlig?) -- home-a.html
* Lage vaktliste                        -- new-shift.html
* Godkjenne overtid i timebank          --
* "Godkjenne" sykdom - feed             -- home-a.html
* Sende inn vaktliste                   --
*
* Alle:
* login                                 -- login.html
* endre passord                         -- resetpassword.html
* generell vaktliste med info           --
* se ledige vakter                      --
* */


var adminlinks = [
    "/html/home-a.html",
    "/html/register.html",
    "/html/user-a.html",
    "/html/user-e.html",
    "/html/new-shift.html",
    "/html/login.html",
    "/html/resetpassword.html",
    "/html/add-users-to-shift.html",
    "/html/calendar-a.html",
    "/html/user-shifts.html",
    "/html/index.html"
];

var employeelinks = [
    "/html/home-e.html",
    "/html/user-e.html",
    "/html/availability.html",
    "/html/availability-select.html",
    "/html/calendar-e.html",
    "/html/login.html",
    "/html/user-shifts.html",
    "/html/resetpassword.html",
    "/html/index.html",
    "/html/messages-e.html"
];

function adminAccess(){
    var currentLocation = window.location.pathname;
    //console.log("location: ", currentLocation);

    var found = $.inArray(currentLocation, adminlinks) > -1;

    if(found){
        //console.log("OK a");
    } else{
        //console.log("FAIL for A go to");
        window.location.replace("/html/index.html");
    }
}

function employeeAccess(){
    var currentLocation = window.location.pathname;
    //console.log("location: ", currentLocation);

    var found = $.inArray(currentLocation, employeelinks) > -1;
    if(found){
        //console.log("OK e");
    } else{
        //console.log("FAIL for E go to");
        window.location.replace("/html/index.html");
    }
}