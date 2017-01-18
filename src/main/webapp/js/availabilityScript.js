/*
$(document).ready(function(){

//$("#calendar-availability").html("<p>hello</p>");

 var tableId = $('#calendar-availability tr');
 var columnId = $('#calendar-availability tr td');
 var cells = document.getElementsByTagName('td');
 for (var i = 0; i < cells.length; i++) {
     cells[i].addEventListener('click', clickHandler);
 }
 function clickHandler() {
    /*for(var i=0; i<moned.length;i++) {
        console.log('moned length');
    }*/
    /*
     console.log(this.textContent);
     if(this.textContent>0) {
         $.ajax({
             url: "/rest/session/",
             type: 'POST',
             data: {
                 identificator: $("#identificator").val(),
                 password: $("#password").val()
             },
             success: login,
             error: invalid
         });
     }
 }
 tableId.click(function() {
    //console.log('clicked');
     //var rowIndex = tableId.index(this); //index relative to the #tableId rows
     //console.log(rowIndex);
 });
});
*/


function login(data){
    console.log("Login", data);


    if(data.isAdmin === true){
        console.log("admin");
        window.location="home-a.html";
    } else{
        console.log("ansatt");
        window.location="home-e.html";
    }
}

function invalid(data){
    //$('.feedback').show();
    //console.log("Invalid", data);
}