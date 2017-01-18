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
        console.log("index = "+i);
        for(j = 0; j < data[i].userBasics.length;j++){
            data[i].userBasics[j]["orderCharacter"] = data[i].orderCharacter;
            users.push(data[i].userBasics[j]);
            user = data[i].userBasics[j];
            console.log(user);
            name = user.firstName + " " + user.lastName;
            html =
                "<div class='watch' data-id='"+user.id+"'>" +
                    "<div class='watch-info'>" +
                        "<p class='lead'>"+name+"</p>" +
                        "<p class='sub'>" +shiftTypes[user.category]+ "</p>" +
                    "</div>" +
                    "<div class='user-data more-info' data-hasInfo='0'></div>" +
                "</div>";
            userListElement.append(html);
            console.log("index = "+i+" J = "+j+"Userbasics"+data[i].userBasics.length);

        }

    }
    setEventListeners();
}

//Updates usertable from search
function getSearchedUsers(pattern){

}
function dropDownUser(user, element){
    var userDataElement = element.children(".user-data");
    console.log(userDataElement.length);
    if(userDataElement.data("hasInfo") != "1") {
        userDataElement.data("hasInfo", "1");
        html =
            "<p><i class='symbol'>" +
                "<i class='material-icons'>panorama_fish_eye</i>" +
                "<i class='material-icons'>mail_outline</i>" +
            "</i>"+ user.email + "</p>" +
            "<p><i class='symbol'>" +
                "<i class='material-icons'>panorama_fish_eye</i>" +
                "<i class='material-icons'>phone</i></i>"
            + user.phoneNumber + "</p>" +
            "<p>100% stilling</p>";
        console.log(html);
        userDataElement.append(html)
    }
    else {
        userDataElement.toggle();
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
                console.log(data);
                dropDownUser(data, watchElement)
            },
            error: function (data) {
                var calendarList = $(".list");
                calendarList.append("<p>" + data + "</p>");
            }
        });
    });
}

$(document).ready(function() {
    var userListElement = $('.list');
    var $search = $('#search');
    $search.on('input', function() {
        var output = [];
        var searchStr = $search.val();
        var temp = [];
        var currentChar = "";
        for (var i = 0; i < users.length; i++) {
            var str = users[i].firstName + " " + users[i].lastName;
            if (str.toLowerCase().match(searchStr.toLowerCase())) {
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

        userListElement.html("");

        for(var i = 0; i < output.length; i++) {
            html =
                "<div class='container-title'>" +
                "<h3>" + output[i].char + " </h3>" +
                "</div>";
            userListElement.append(html);
            for (var j = 0; j < output[i].data.length; j++) {


                var user = output[i].data[j];
                console.log(user);
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


   });
});