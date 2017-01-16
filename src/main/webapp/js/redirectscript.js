/**
 * Created by ingvildbroen on 12.01.2017.
 */

$(document).ready(function(){
    $.ajax({
        url: "/rest/session/login",
        type: 'GET',
        data: {
            isAdmin: "true"
        },
        success: admin,
        error: employee
    });
});

function admin(data){
    console.log("admin");
}

function employee(data){
    console.log("employee");

}