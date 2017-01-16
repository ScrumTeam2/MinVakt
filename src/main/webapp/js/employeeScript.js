/**
 * Created by evend on 1/13/2017.
 */

$.ajax({
    //     url: "rest/shift/user/"+userId,
    url: "../rest/user/",
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
    var shiftTypes, userListElement, html, name, i, j, user;
    shiftTypes = {"ADMIN" : "Administrasjon", "ASSISTANT" : "Assistent", "HEALTH_WORKER" : "Helsemedarbeider", "NURSE" : "Sykepleier"};
    userListElement = $(".list");
    userListElement.html = "";
    for(i = 0; i<data.length;i++){
        html =
            "<div class='container-title'>" +
                "<h3>" +data[i].orderCharacter+ " </h3>" +
            "</div>";
        console.log(html);
        console.log(data[i]);
        console.log(data[i].userBasics);
        userListElement.append(html);
        console.log("index = "+i);
        for(j = 0; j < data[i].userBasics.length;j++){
            user = data[i].userBasics[j];
            console.log(user);
            name = user.firstName + " " + user.lastName;
            html =
                "<div class='watch'>" +
                    "<div class='watch-info'>" +
                        "<p class='lead'>"+name+"</p>" +
                        "<p class='sub'>" +shiftTypes[user.category]+ "</p>" +
                    "</div>" +
                "</div>";
            userListElement.append(html);
            console.log("index = "+i+" J = "+j+"Userbasics"+data[i].userBasics.length);

        }
    }
}

//Updates usertable from search
function getSearchedUsers(pattern){

}