/**
 * Created by ingvildbroen on 11.01.2017.
 */


$(document).ready(function(){

    $('#userBtn').click(function(e){
        e.preventDefault();
        var emptyField = true;

        var $first = $('#firstname').value();
        var $last = $('#lastname').value();
        var $percent = $('#percentage').value();
        var $email = $('#email').value();
        var $phone = $('#phone').value();
        var $category = $('#category').value();

        if(!$identificator.val()){
            $identificator.addClass('error');
            emptyField = false;
        }

        if(!$password.val()){
            $password.addClass('error');
            emptyField = false;
        }


        if(emptyField){
            $.ajax({
                url: "/rest/admin/createuser",
                type: 'POST',
                data: {
                    firstName: $('#firstname').val(),
                    lastName: $('#lastname').val(),
                    email: $('#email').val(),
                    phonenumber: $('#phone').val(),
                    category: $('#category').val(),
                    workPercentage: $('#percentage').val()
                },
                success: addUser,
                error: invalidField
            });
        }
    });
});

function addUser(data){
    console.log("Login", data);

    $('.modal').show();

}

function invalidField(data){
    $('.feedback').show();
    console.log("Invalid", data);
}



//close modal
$('#userCloseBtn').click(function() {
    $('.modal').hide();
    $('.register-form')[0].reset();
});

//go to submitted users profile
$('#userViewBtn').click(function() {
    window.location="user-a.html";
});


//close modal when clicking outside of the modal
var modal = document.getElementById('userModal');
window.onclick = function(event) {
    if (event.target == modal) {
        modal.hide();
    }
}