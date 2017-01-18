/**
 * Created by ingvildbroen on 18.01.2017.
 */

$(document).ready(function(){
    if(sessionStorage.getItem("SessionId")){
        if(sessionStorage.getItem("SessionIdCat") === 'ADMIN'){
            window.location = "home-a.html";
            console.log("Already logged in as admin");
        } else{
            window.location = "home-e.html";
            console.log("Already logged in as employee");
        }
    } else{
        window.location = "login.html";
        console.log("need to login");
    }
});