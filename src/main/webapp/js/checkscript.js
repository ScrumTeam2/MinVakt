/**
 * Created by ingvildbroen on 12.01.2017.
 */

$(document).ready(function(){

    if(sessionStorage.getItem("SessionId")){
        var expire = sessionStorage.getItem("SessionExpires");
        var date = new Date();
        console.log("expire: ", expire);
        console.log("date: ", date);
        console.log("date.get: ", date.getTime());
        var timeNow = date.getTime();

        if(expire <= timeNow){
            console.log("yo");
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

function renewSession(){
    console.log("renew");
    var date = new Date();
    date.setTime(date.getTime() + (2*60*60*1000));
    sessionStorage.SessionExpires = date;
}

function redirect(){
    console.log("login again");
    sessionStorage.clear();
    window.location="login.html";
}