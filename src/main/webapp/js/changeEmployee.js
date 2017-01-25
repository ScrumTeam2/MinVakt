/**
 * Created by olekristianaune on 20.01.2017.
 */

/**
 * Created by evend on 1/13/2017.
 */

var shiftTypes = {
    "ADMIN" : "Administrasjon",
    "ASSISTANT" : "Assistent",
    "HEALTH_WORKER" : "Helsefagarbeider",
    "NURSE" : "Sykepleier"
};

var allUsers = [];
var categoryUsers = [];
var returnToEditPage = getUrlParameter("edit") === "1";
var user = getUrlParameter("user");
var shift = getUrlParameter("shift");
var category;
var same = true;


$(document).ready(function() {
    var $sameCategory = $('#sameCategory');
    var $name = $('#name');
    var $cat = $('#category');

    $.ajax({
        url: "/rest/user/" + user,
        type: "GET",
        success: function(data) {
            category = data.category;
            $name.text(data.firstName + " " + data.lastName);
            $cat.text(category);
            if ($sameCategory[0].checked) {
                loadSuggestedCategory(category);
                loadCategory(category);
            } else {
                loadSuggestedAll(category);
                loadAll();
            }
        },
        error: function(e) {
            console.error( "Ingen bruker med id " + user, e );
        }
    });

    $sameCategory.on('change', function() {
        if ($sameCategory[0].checked) {
            same = true;
            //if (categoryUsers.length === 0) {
                loadSuggestedCategory(category);
                loadCategory(category);
            //}
        } else {
            same = false;
            //if (allUsers.length === 0) {
                loadSuggestedAll();
                loadAll();
            //}
        }
    });

    var $search = $('#search');
    $search.on('input keypress', function() {
        search($search.val());
        setUrlParameter("search", $search.val());
    });


});

function loadSuggestedCategory(catName) {
    console.log(catName);
    $.ajax({
        url: "/rest/availability/shift/" + shift + "?category=" + catName + "&limitByCategory=true",
        type: "GET",
        success: function(data) {
            console.log("Data by category", data);
            displayUsers(data);
        },
        error: function(e) {
            console.error("Couldn't get data from category " + catName, e);
        }
    });
}

function loadCategory(catName) {
    console.log(catName);
    $.ajax({
        url: "/rest/user/category?category=" + catName,
        type: 'GET',
        success: function(data) {
            console.log("All in category",data);
            for (var i = 0; i < data.length; i++) {
                categoryUsers.push(data[i]);
            }
        },
        error: function (e) {
            console.error("loadCategory", e);
        }
    });
}

function loadSuggestedAll(catName) {
    $.ajax({
        url: "/rest/availability/shift/" + shift + "?category=" + catName + "&limitByCategory=false",
        type: "GET",
        success: function(data) {
            console.log("Data by category", data);
            displayUsers(data);
        },
        error: function(e) {
            console.error("Couldn't get data from category " + catName, e);
        }
    });
}

function loadAll() {
    $.ajax({
        url: "/rest/user/",
        type: 'GET',
        dataType: 'json',
        success: function(data) {
            for (var i = 0; i < data.length; i++) {
                var letterArray = data[i].userBasics;
                for (var j = 0; j < letterArray.length; j++) {
                    allUsers.push(letterArray[j]);
                }
            }
        },
        error: function (e) {
            console.error("loadAll", e);
        }
    });
}

function displayUsers(data) {
    var userListElement = $(".list");
    var html, name, i, j, user;

    userListElement.html("");
    html =`
                <div class='container-title'>
                    <h3>Ansatte - anbefalt bytte</h3>
                </div>`;
    userListElement.append(html);

    for(i = 0; i<data.length;i++){

        user = data[i];

        name = user.firstName + " " + user.lastName;

        html = `
                    <div class='watch' data-id="${user.id}">
                        <div class='watch-info'>
                            <p class='lead'>${name}</p>
                            <p class='sub'>${shiftTypes[user.category]}</p>
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

// TODO: Fix data retrievel so it returns the same way as the other data
/*
function getUsersForSearch(data){
    var userListElement = $(".list");
    var html, name, i, j, user;

    userListElement.html = "";
    html =`
                <div class='container-title'>
                    <h3>Ansatte</h3>
                </div>`;
    userListElement.append(html);

    for(i = 0; i<data.length;i++){
        for(j = 0; j < data[i].userBasics.length; j++){

            users.push(data[i].userBasics[j]);
            user = data[i].userBasics[j];

            name = user.firstName + " " + user.lastName;

            html = `
                        <div class='watch' data-id="${user.id}">
                            <div class='watch-info'>
                                <p class='lead'>${name}</p>
                                <p class='sub'>${shiftTypes[user.category]}</p>
                            </div>
                            <a href="#" class="link changeEmployee">Endre</a>
                        </div>`;

            userListElement.append(html);

        }

    }

    $(".changeEmployee").click(function(e) {
        e.preventDefault();
        var changeId = $(e.currentTarget).parent().data("id");
        addToShift(changeId);
    });
}
*/

function search(searchStr) {
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
                    <h3>Ansatte</h3>
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
                        <p class='sub'>${shiftTypes[user.category]}</p>
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