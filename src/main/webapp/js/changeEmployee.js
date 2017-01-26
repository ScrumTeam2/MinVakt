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
var suggestedCategoryUsers = [];
var allUsers = [];
var categoryUsers = [];
var headers = [];
var returnToEditPage = getUrlParameter("edit") === "1";
var user = getUrlParameter("user");
var shift = getUrlParameter("shift");
var $sameCategory;
var category;
var same = true;
var test = false;


$(document).ready(function() {
    $sameCategory = $('#sameCategory');
    var $name = $('#name');
    var $cat = $('#category');

    $.ajax({
        url: "/rest/user/" + user,
        type: "GET",
        success: function(data) {
            //console.log("USER DATA", data);
            category = data.category;
            $name.text(data.firstName + " " + data.lastName);
            $cat.text(category);
            headers = [ "Tilgjengelige " + userTypesPlural[category] + " for dette skiftet",
                        "Tilgjengelige ansatte for dette skiftet",
                        "Søker etter " + userTypesPlural[category] + " med navn ",
                        "Søker etter ansatte med navn "];
            loadAll(category);
        },
        error: function(e) {
            console.error( "Ingen bruker med id " + user, e );
        }
    });

    $sameCategory.on('change', function() {
        if ($sameCategory[0].checked) {
            same = true;
            showCategory();
        } else {
            same = false;
            showAll();
        }
    });

    var $search = $('#search');
    $search.on('input keypress', function() {
        setUrlParameter("search", $search.val());
        if (same) {
            showCategory();
        } else {
            showAll()
        }
    });


});

// Load all data
function loadAll(catName) {
    // LOAD SUGGESTED USERS BY CATEGORY
    $.ajax({
        url: "/rest/availability/shift/" + shift + "?category=" + catName + "&limitByCategory=true",
        type: "GET",
        success: function(data) {
            //console.log("LOAD SUGGESTED USERS BY CATEGORY", data);
            suggestedCategoryUsers = data;

            if ($sameCategory[0].checked) {
                showCategory();
            }
        },
        error: function(e) {
            console.error("Couldn't get data from category " + catName, e);
        }
    });

    // LOAD SUGGESTED USERS WITHOUT CATEGORY
    $.ajax({
        url: "/rest/availability/shift/" + shift + "?category=" + catName + "&limitByCategory=false",
        type: "GET",
        success: function(data) {
            //console.log("LOAD SUGGESTED USERS WITHOUT CATEGORY", data);
            suggestedAllUsers = data;

            if (!$sameCategory[0].checked) {
                showAll();
            }
        },
        error: function(e) {
            console.error("Couldn't get data from category " + catName, e);
        }
    });

    // LOAD ALL USERS FROM A CATEGORY
    $.ajax({
        url: "/rest/user/category?category=" + catName,
        type: 'GET',
        success: function(data) {
            //console.log("LOAD ALL USERS FROM A CATEGORY", data);
            categoryUsers = data;
        },
        error: function (e) {
            console.error("loadCategory", e);
        }
    });

    // LOAD ALL USERS
    $.ajax({
        url: "/rest/user/",
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            //console.log("LOAD ALL USERS", data);
            for (var i = 0; i < data.length; i++) {
                var letterArray = data[i].userBasics;
                allUsers.push.apply(allUsers, letterArray);
            }
        },
        error: function (e) {
            console.error("loadAll", e);
        }
    });
}

function showCategory() {
    if (getUrlParameter("search") === "") {
        displayUsers(headers[0], suggestedCategoryUsers);
    } else {
        search(headers[2], getUrlParameter("search"));
    }
}

function showAll() {
    if (getUrlParameter("search") === "") {
        displayUsers(headers[1], suggestedAllUsers);
    } else {
        search(headers[3], getUrlParameter("search"));
    }
}

function displayUsers(header, data) {
    var userListElement = $(".list");
    var html, name, i, j, user;

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
                        <a href="#" class="link changeEmployee">Endre</a>
                    </div>`;

        userListElement.append(html);


    }

    $(".changeEmployee").click(function(e) {
        e.preventDefault();
        var changeId = $(e.currentTarget).parent().data("id");
        addToShift(changeId);
    });
}

function search(header, searchStr) {
    var userListElement = $('.list');
    var output = [];
    var users;
    if (same) {
        users = categoryUsers;
    } else {
        users = allUsers;
    }
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
        //console.log(user);
        var name = user.firstName + " " + user.lastName;
        var html =`
                <div class='watch' data-id='${user.id}'>
                    <div class='watch-info'>
                        <p class='lead'>${name}</p>
                        <p class='sub'>${userTypes[user.category]}</p>
                    </div>
                    <a href="#" class="link changeEmployee">Endre</a>
                </div>`;
        userListElement.append(html);

    }
}

function addToShift(id) {
    var shiftId = getUrlParameter("shift");
    var oldUserId = getUrlParameter("user");
    console.log(shiftId);
    console.log(id);
    $.ajax({
        url: "/rest/shift/" + shiftId + "/replaceuser/",
        type: 'POST',
        data: {
            oldUserId: oldUserId,
            newUserId: id
        },
        success: function(data) {
            console.log("Yay!", data);
            window.location = returnToEditPage ? "edit-shift.html?id=" + shiftId : "add-users-to-shift.html";
        },
        error: function (data) {
            console.log("Ney!", data);
        }
    });
}