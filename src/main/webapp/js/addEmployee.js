/**
 * Created by olekristianaune on 20.01.2017.
 */

/**
 * Created by evend on 1/13/2017.
 */

var userTypes = {
    "ADMIN" : "Administrasjon",
    "ASSISTANT" : "Assistent",
    "HEALTH_WORKER" : "Helsefagarbeider",
    "NURSE" : "Sykepleier"
};

var userTypesPlural = {
    "ADMIN" : "administratorer",
    "ASSISTANT" : "assistenter",
    "HEALTH_WORKER" : "helsefagarbeidere",
    "NURSE" : "sykepleiere"
};

var suggestedAllUsers = [];
var allUsers = [];
var headers = [];
var shift = getUrlParameter("shift");
var feedId = getUrlParameter("feedId");
var userId = getUrlParameter("userId");


$(document).ready(function() {

    headers = [ "Tilgjengelige ansatte for dette skiftet",
                "Søker etter ansatte med navn "];
    loadAll();

    var $search = $('#search');
    $search.on('input keypress', function() {
        setUrlParameter("search", $search.val());
        showAll()
    });


});

// Load all data
function loadAll() {

    // LOAD SUGGESTED USERS WITHOUT CATEGORY
    $.ajax({
        url: "/rest/availability/shift/" + shift + "?category=ASSISTANT&limitByCategory=false",
        type: "GET",
        success: function(data) {
            suggestedAllUsers = data;
            showAll();
        },
        error: function(e) {
            console.error("(58) Could not load users", e);
        }
    });

    // LOAD ALL USERS
    $.ajax({
        url: "/rest/user/",
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            for (var i = 0; i < data.length; i++) {
                var letterArray = data[i].userBasics;
                allUsers.push.apply(allUsers, letterArray);
            }
        },
        error: function (e) {
            console.error("(74) Could not load users", e);
        }
    });
}

function showAll() {
    if (!getUrlParameter("search")) {
        displayUsers(headers[0], suggestedAllUsers);
    } else {
        search(headers[1], getUrlParameter("search"));
    }
}

function displayUsers(header, data) {
    var userListElement = $(".list");
    var html, name, i, user;

    userListElement.html("");
    html =`
                <div class='container-title'>
                    <h3>${header}</h3>
                </div>`;
    userListElement.append(html);

    for(i = 0; i<data.length;i++){

        user = data[i];

        name = user.firstName + " " + user.lastName;

        html = `
                    <div class='watch' data-id="${user.id}">
                        <div class='watch-info'>
                            <p class='lead'>${name}</p>
                            <p class='sub'>${userTypes[user.category]}</p>
                        </div>
                        <a href="#" class="link addEmployee">Legg til</a>
                    </div>`;

        userListElement.append(html);


    }

    $(".addEmployee").click(function(e) {
        e.preventDefault();
        var changeId = $(e.currentTarget).parent().data("id");
        addToShift(changeId);
    });
}

function search(header, searchStr) {
    var userListElement = $('.list');
    var output = [];
    var users = allUsers;
    for (var i = 0; i < users.length; i++) {
        var str = users[i].firstName;
        str += " ";
        str += users[i].lastName;

        str = str.toLowerCase();
        searchStr = searchStr.toLowerCase();

        if (str.includes(searchStr)) {
            output.push(users[i]);
        }
    }

    userListElement.html("");
    html =`
                <div class='container-title'>
                    <h3>${header + '"' + searchStr + '"'}</h3>
                </div>`;
    userListElement.append(html);

    for(var i = 0; i < output.length; i++) {

        var user = output[i];
        var name = user.firstName + " " + user.lastName;
        var html =`
                <div class='watch' data-id='${user.id}'>
                    <div class='watch-info'>
                        <p class='lead'>${name}</p>
                        <p class='sub'>${userTypes[user.category]}</p>
                    </div>
                    <a href="#" class="link addEmployee">Legg til</a>
                </div>`;
        userListElement.append(html);

    }

    $(".addEmployee").click(function(e) {
        e.preventDefault();
        var changeId = $(e.currentTarget).parent().data("id");
        addToShift(changeId);
    });
}

function addToShift(id) {
    var shiftId = getUrlParameter("shift");
    $.ajax({
        url: "/rest/shift/" + shiftId + "/user/" + id,
        type: 'POST',
        success: function() {
            window.location = "edit-shift.html?id=" + shiftId+"&feedId="+feedId+"&userId="+userId;
        },
        error: function (e) {
            console.error(e);
        }
    });
}