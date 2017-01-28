/**
 * Created by ingvildbroen on 12.01.2017.
 */

$(document).ajaxError(function (event, jqXHR) {
    // Called when we get an AJAX-error anywhere on the site
    if (jqXHR.status == 401) {
        // Not authorized, log out
        clearLogin();
    }
});

$(document).ready(function () {
    addNameToTop();
    var currPage = window.location.pathname.replace("/html/", "");
    var $menu = $('#popup_menu');
    var anchors = $menu.find($("a"));

    for (var i = 0; i < anchors.length; i++) {
        var $link = $(anchors[i]);
        if ($link.attr("href") === currPage) {
            $link.addClass("active");
        }
    }

    if (sessionStorage.getItem("SessionId")) {
        var expire = sessionStorage.getItem("SessionExpires");
        var date = new Date();
        var timeNow = date.getTime();

        var userCategory = sessionStorage.getItem("SessionIdCat");
        if (userCategory === 'ADMIN') {
            console.log("admin");
            adminAccess();
        } else if (userCategory === 'HEALTH_WORKER' || userCategory === 'ASSISTANT' || userCategory === 'NURSE') {
            console.log("assistant, nurse or health");
            console.log("path ", window.location.pathname);
            employeeAccess();
        } else {
            console.log("category not found", userCategory);
            clearLogin();
        }

        if (expire <= timeNow) {
            $.ajax({
                url: "/rest/session/check",
                type: 'GET',
                success: renewSession,
                error: clearLogin
            });
        }
    } else {
        clearLogin();
    }
});

//Menu button
$('#menu_icon').on("click", function (e) {
    e.preventDefault();
    var $menu = $('#popup_menu');
    $menu.toggle();
    var $more = $('#popup_more');
    if ($more.is(":visible")) {
        $more.hide();
    }
});

//More button
$('#more_icon').on("click", function (e) {
    e.preventDefault();
    var $more = $('#popup_more');
    $more.toggle();
    var $menu = $('#popup_menu');
    if ($menu.is(":visible")) {
        $menu.hide();
    }
});

$('#logout').on("click", function (e) {
    e.preventDefault();
    logOut();
});

function logOut() {
    $.ajax({
        url: "/rest/session/log_out",
        type: 'GET',
        success: clearLogin
    });
    //console.log("log out function");
}


function renewSession() {
    //console.log("renew");
    var date = new Date();
    date.setTime(date.getTime() + (2 * 60 * 60 * 1000));
    sessionStorage.SessionExpires = date;
}

function clearLogin() {
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
    "/html/index.html",
    "/html/add-employee.html",
    "/html/change-employee.html",
    "/html/change-password.html",
    "/html/edit-shift.html",
    "/html/user-profile.html"
];

var employeelinks = [
    "/html/home-e.html",
    "/html/user-e.html",
    "/html/availability.html",
    "/html/availability-select.html",
    "/html/login.html",
    "/html/user-shifts.html",
    "/html/resetpassword.html",
    "/html/index.html",
    "/html/messages-e.html",
    "/html/change-password.html",
    "/html/register-overtime.html"
];

function adminAccess() {
    var currentLocation = window.location.pathname;
    //console.log("location: ", currentLocation);

    var found = $.inArray(currentLocation, adminlinks) > -1;

    if (found) {
        //console.log("OK a");
    } else {
        //console.log("FAIL for A go to");
        window.location.replace("/html/index.html");
    }
}

function employeeAccess() {
    var currentLocation = window.location.pathname;
    //console.log("location: ", currentLocation);

    var found = $.inArray(currentLocation, employeelinks) > -1;
    if (found) {
        //console.log("OK e");
    } else {
        //console.log("FAIL for E go to");
        window.location.replace("/html/index.html");
    }
}


// URL PARAMETERS
function setUrlParameter(key, value) {
    key = encodeURI(key);
    value = encodeURI(value);

    var kvp = document.location.search.substr(1).split('&');

    var i = kvp.length;
    var x;
    while (i--) {
        x = kvp[i].split('=');

        if (x[0] == key) {
            x[1] = value;
            kvp[i] = x.join('=');
            break;
        }
    }

    if (i < 0) {
        kvp[kvp.length] = [key, value].join('=');
    }

    //Set back url
    window.history.replaceState('', '', window.location.href.split("?")[0] + "?" + kvp.join('&'));
}

var getUrlParameter = function getUrlParameter(sParam) {
    var sPageURL = decodeURIComponent(window.location.search.substring(1)),
        sURLVariables = sPageURL.split('&'),
        sParameterName,
        i;

    for (i = 0; i < sURLVariables.length; i++) {
        sParameterName = sURLVariables[i].split('=');

        if (sParameterName[0] === sParam) {
            return sParameterName[1] === undefined ? true : decodeURI(sParameterName[1]);
        }
    }
};

function convertDate(dateInput) {
    var monthNames = [
        "januar", "februar",
        "mars", "april", "mai",
        "juni", "juli", "august",
        "september", "oktober", "november",
        "desember"];

    var dayNames = ["Søndag", "Mandag", "Tirsdag",
        "Onsdag", "Torsdag", "Fredag",
        "Lørdag"];

    var date = new Date(dateInput);
    var day = date.getDate();
    var monthIndex = date.getMonth();
    var dayIndex = date.getDay();

    return dayNames[dayIndex] + " " + day + ". " + monthNames[monthIndex];
}

function addNameToTop() {
    $("#more_icon").prepend(sessionStorage.firstName + " " + sessionStorage.lastName);
}