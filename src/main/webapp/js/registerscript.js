/**
 * Created by ingvildbroen on 11.01.2017.
 */

$(document).ready(function(){

    sessionStorage.getItem("SessionId");
    console.log(sessionStorage.getItem("SessionId"));
    console.log(sessionStorage.getItem("SessionExpires"));

    var userValue = $(".user-type:checked").val();

    $(".user-type").on('change', function() {
        userValue = $(".user-type:checked").val();

        if(userValue === "admin"){
            showAdminInput();
        } else{
            showEmployeeInput();
        }
    });


    $('#userBtn').click(function(e){
        e.preventDefault();

        var emptyField = false;

        var $first = $('#firstname');
        var $last = $('#lastname');
        var $percent = $('#percentage');
        var $email = $('#email');
        var $phone = $('#phone');
        var $category = $('#category');

        if(!$first.val()){
            $first.addClass('error');
            emptyField = true;
        }

        if(!$last.val()){
            $last.addClass('error');
            emptyField = true;
        }

        if(!$email.val()){
            $email.addClass('error');
            emptyField = true;
        }

        if(!$phone.val()){
            $phone.addClass('error');
            emptyField = true;
        }

        var formData;

        console.log(emptyField);
        console.log(userValue);

        if(userValue === "admin"){
            if(!emptyField){
                console.log("admin her");
                formData = {
                    "firstName": $first.val(),
                    "lastName": $last.val(),
                    "email": $email.val(),
                    "phoneNumber": $phone.val(),
                    "category": 'ADMIN'
                    //"workpercentage": 100 eller null
                };
            }

        } else{

            if(!$category.val()){
                $category.addClass('error');
                emptyField = true;
            }
            /*if(!$percent.val()){
             $percent.addClass('error');
             emptyField = true;
             }*/

            if(!emptyField){
                formData = {
                    "firstName": $first.val(),
                    "lastName": $last.val(),
                    "email": $email.val(),
                    "phoneNumber": $phone.val(),
                    "category": $category.val()
                };
                console.log(JSON.stringify(formData));
            }
        }

        $.ajax({
            url: "/rest/admin/createuser",
            type: 'POST',
            dataType: "json",
            contentType: "application/json",
            data: JSON.stringify(formData),
            success: addUser,
            error: invalidField
        });
    });
});

function showAdminInput(){
    var $select = $('.select-container');
    var $percent = $('#percentage');
    $select.addClass("hide");
    $percent.addClass("hide");
}


function showEmployeeInput(){
    var $select = $('.select-container');
    var $percent = $('#percentage');
    $select.removeClass("hide");
    $percent.removeClass("hide");
}

function addUser(data){
    console.log("OK", data);
    console.log("Adduser");
    $('.result').text("Bruker ble laget med passord: " + data.password);

    //go to submitted users profile
    $('#userViewBtn').click(function() {
        window.location="user-a.html";
    });

    $('.modal').show();
}

function invalidField(data){
    $('.result').text("Email eller passord er allerede i bruk.");
    $('#userViewBtn').hide();
    console.log("Invalid data", data);
}

//close modal
$('#userCloseBtn').click(function() {
    $('.modal').hide();
    $('.register-form')[0].reset();
});




//close modal when clicking outside of the modal
var modal = document.getElementById('userModal');
window.onclick = function(event) {
    if (event.target == modal) {
        modal.hide();
        $('.register-form')[0].reset();
    }
};