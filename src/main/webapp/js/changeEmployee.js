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

var users = [];

$(document).ready(function() {
    $.ajax({
        //     url: "rest/shift/user/"+userId,
        url: "/rest/user/",
        type: 'GET',
        dataType: 'json',
        success: getUsersForSearch,
        error: function (data) {
            var calendarList = $(".list");
            calendarList.append(`<p>${data}</p>`);
        }
    });
    //Gets first call to database to collect all users
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

    var $search = $('#search');
    $search.on('input keypress', function() {
        search($search.val());
        window.history.replaceState('', '', window.location.href.split("?")[0] + "?search=" + $search.val());
        setEventListeners();
    });


    function search(searchStr) {
        var userListElement = $('.list');
        var output = [];
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
                </div>`;
            userListElement.append(html);

        }
    }

});

function addToShift(id) {
    var shiftId = getUrlParameter("shift");
    console.log(shiftId);
    console.log(id);
    $.ajax({
        url: "/rest/shift/" + shiftId + "/user/" + id,
        type: 'POST',
        data: {
            oldUserId: 1,
            newUserId: id
        },
        success: function(data) {
            console.log("Yay!", data);
        },
        error: function (data) {
            console.log("Ney!", data);
        }
    });
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