/**
 * Created by ingvildbroen on 11.01.2017.
 */


/*
* Modal for submitting user
*/

//function for submitting user
function submitForm(){
    var first = $('#firstname').value();
    var last = $('#lastname').value();
    var percent = $('#percentage').value();
    var email = $('#email').value();
    var phone = $('#phone').value();
    var category = $('#category').value();


    $('#firstname').addClass('error');
    $('.modal').show();
    return false;

    if(first == null){
        first.addClass('error');
    }


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