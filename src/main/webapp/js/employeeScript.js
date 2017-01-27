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
            calendarList.append("<p>" + data + "</p>");
        }
    });
    //Gets first call to database to collect all users
    function getUsersForSearch(data){
        var userListElement = $(".list");
        var html, name, i, j, user;

        userListElement.html = "";
        for(i = 0; i<data.length;i++){
            html =
                "<div class='container-title'>" +
                    "<h3>" +data[i].orderCharacter+ " </h3>" +
                "</div>";
            //console.log(html);
            //console.log(data[i]);
            //console.log(data[i].userBasics);
            userListElement.append(html);
            //console.log("index = "+i);
            var category = sessionStorage.getItem("SessionIdCat");
            for(j = 0; j < data[i].userBasics.length;j++) {
                data[i].userBasics[j]["orderCharacter"] = data[i].orderCharacter;
                users.push(data[i].userBasics[j]);
                user = data[i].userBasics[j];
                //console.log(user);
                name = user.firstName + " " + user.lastName;
                html =
                    "<div class='watch' data-id='" + user.id + "'>" +
                    "<div class='watch-info'>" +
                    "<p class='lead'>" + name + "</p>" +
                    "<p class='sub'>" + shiftTypes[user.category] + "</p>" +
                    "</div>";
                if (category == 'ADMIN') {
                    html += "<div><a href='/edit-user.html?userId=" + user.id + "' class='link'>Endre</a></div>";
                }
                html += "<div class='user-data more-info' data-hasInfo='0'></div>" +
                    "</div>";
                userListElement.append(html);
                //console.log("index = "+i+" J = "+j+"Userbasics"+data[i].userBasics.length);

            }

        }
        if (getUrlParameter("search")) {
            $search.val(getUrlParameter("search"));
            search(getUrlParameter("search"));
        }
        setEventListeners();
    }

    function dropDownUser(user, element){
        var userDataElement = element.children(".user-data");
        userDataElement.addClass("loaded");
        //console.log(userDataElement.length);
        if(userDataElement.data("hasInfo") != "1") {
            userDataElement.data("hasInfo", "1");
            html =
                "<p class='more-info__text'><i class='symbol'>" +
                    "<i class='material-icons'>panorama_fish_eye</i>" +
                    "<i class='material-icons'>mail_outline</i>" +
                "</i>"+ user.email + "</p>" +
                "<p class='more-info__text'><i class='symbol'>" +
                    "<i class='material-icons'>panorama_fish_eye</i>" +
                    "<i class='material-icons'>phone</i></i>"
                + user.phoneNumber + "</p>" +
                "<p class='more-info__text'>"+(user.workPercentage*100)+"% stilling</p>";
            //console.log(html);
            userDataElement.append(html);
            console.log(user.workPercentage*100);
        }
        else {
            userDataElement.slideToggle(200);
        }

    }

    function setEventListeners(){
        $(".watch").click(function () {
            var watchElement = $(this);
            $.ajax({
                url: "/rest/user/"+watchElement.data("id"),
                type: 'GET',
                dataType: 'json',
                success: function (data) {
                    //console.log(data);
                    dropDownUser(data, watchElement);
                },
                error: function (data) {
                    var calendarList = $(".list");
                    calendarList.append("<p>" + data + "</p>");
                }
            });
        });
    }

    var $search = $('#search');
    $search.on('input keypress', function() {
        search($search.val());
        setUrlParameter("search",$search.val());
        setEventListeners();
    });


    function search(searchStr) {
        var userListElement = $('.list');
        var output = [];
        var temp = [];
        var currentChar = "";
        for (var i = 0; i < users.length; i++) {
            var str = users[i].firstName;
            str += " ";
            str += users[i].lastName;

            str = str.toLowerCase();
            searchStr = searchStr.toLowerCase();

            //console.log(str, searchStr);
            if (str.includes(searchStr)) {
                //console.log("Match!");
                if (users[i].orderCharacter !== currentChar) {
                    output.push({
                        char : currentChar,
                        data : temp
                    });
                    currentChar = users[i].orderCharacter;
                    temp = [];
                }
                temp.push(users[i]);
            }
        }
        output.push({
            char : currentChar,
            data : temp
        });

        userListElement.html("");

        for(var i = 0; i < output.length; i++) {
            html =
                "<div class='container-title'>" +
                "<h3>" + output[i].char + " </h3>" +
                "</div>";
            userListElement.append(html);
            for (var j = 0; j < output[i].data.length; j++) {


                var user = output[i].data[j];
                //console.log(user);
                var name = user.firstName + " " + user.lastName;
                var html =
                    "<div class='watch' data-id='" + user.id + "'>" +
                    "<div class='watch-info'>" +
                    "<p class='lead'>" + name + "</p>" +
                    "<p class='sub'>" + shiftTypes[user.category] + "</p>" +
                    "</div>" +
                    "<div class='user-data more-info' data-hasInfo='0'></div>" +
                    "</div>";
                userListElement.append(html);

            }
        }
    }

});