/**
 * Created by evend on 1/27/2017.
 */
$(document).ready(function () {
    $.ajax({
        url: "/rest/user/"+getUrlParameter("userId"),
        type: 'GET',
        success: fillUserData,
        error: function (data) {
            console.log("Not able to get userdata")
        }
    });
    addDepartments();
});
function fillUserData(data){
    console.log(data);
    console.log(data.firstName);

    var $firstname = $("#firstname");
    var $lastname = $("#lastname");
    var $email = $("#email");
    var $phone = $("#phone");
    var $percentage = $("#percentage");

    $firstname.val(data.firstName);
    $lastname.val(data.lastName);
    $email.val(data.email);
    $phone.val(data.phoneNumber);
    $percentage.val(data.workPercentage * 100);
    $('# option[value='+data.category+']').attr('selected','selected');
}
function addDepartments() {

}