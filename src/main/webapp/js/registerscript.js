/**
 * Created by ingvildbroen on 11.01.2017.
 */
var createSuccess = false;

$(document).ready(function(){

    var userValue = $(".user-type:checked").val();

    $(".user-type").on('change', function() {
        userValue = $(".user-type:checked").val();

        var $first = $('#firstname');
        var $last = $('#lastname');
        var $percent = $('#percentage');
        var $email = $('#email');
        var $phone = $('#phone');
        var $category = $('#category');

        $first.removeClass('error').parent().attr('data-content', '');
        $last.removeClass('error').parent().attr('data-content', '');
        $percent.removeClass('error').parent().attr('data-content', '');
        $email.removeClass('error').parent().attr('data-content', '');
        $phone.removeClass('error').parent().attr('data-content', '');
        $category.removeClass('error').parent().attr('data-content', '');

        if(userValue === "admin"){
            showAdminInput();
        } else{
            showEmployeeInput();
        }
    });


    $('#userBtn').click(function(e){
        e.preventDefault();

        var formError = false;

        var $first = $('#firstname');
        var $last = $('#lastname');
        var $percent = $('#percentage');
        var $email = $('#email');
        var $phone = $('#phone');
        var $category = $('#category');

        $first.removeClass('error').parent().attr('data-content', '');
        $last.removeClass('error').parent().attr('data-content', '');
        $percent.removeClass('error').parent().attr('data-content', '');
        $email.removeClass('error').parent().attr('data-content', '');
        $phone.removeClass('error').parent().attr('data-content', '');
        $category.removeClass('error').parent().attr('data-content', '');

        if(!$first.val()){
            $first.addClass('error').parent().attr('data-content', 'Du må fylle inn fornavn.');
            formError = true;
        }
        if(!$last.val()){
            $last.addClass('error').parent().attr('data-content', 'Du må fylle inn etternavn.');
            formError = true;
        }
        if(!$email.val() || !(/[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test($email.val()))){
            $email.addClass('error');
            if(!$email.val()) {
                $email.parent().attr('data-content', 'Du må fylle inn e-post.');
            } else {
                $email.parent().attr('data-content', 'E-posten du skrev inn er ikke gyldig.');
            }
            formError = true;
        }
        if(!$phone.val() || !(/^[0-9]{8,8}$/.test($phone.val()))){
            $phone.addClass('error');
            if(!$phone.val()) {
                $phone.parent().attr('data-content', 'Du må fylle inn mobilnummer.');
            } else {
                $phone.parent().attr('data-content', 'Mobilnummeret du skrev inn er ikke gyldig.');
            }
            formError = true;
        }

        var formData;

        console.log(formError);
        console.log(userValue);

        if(userValue === "admin"){
            if(!formError){
                console.log("Submitting admin");
                formData = {
                    "firstName": $first.val(),
                    "lastName": $last.val(),
                    "email": $email.val(),
                    "phoneNumber": $phone.val(),
                    "category": 'ADMIN',
                    "workPercentage": 1
                };
                submitUser(formData);
            }

        } else{

            if(!$category.val()){
                $category.addClass('error').parent().attr('data-content', 'Du må velge en kategori.');
                formError = true;
            }
            if(!$percent.val() || isNaN($percent.val()) || $percent.val() < 0 || $percent.val() > 100){
                $percent.addClass('error');
                if(!$percent.val()) {
                    $percent.parent().attr('data-content', 'Du må fylle inn stillingsprosent.');
                } else if (isNaN($percent.val())) {
                    $percent.parent().attr('data-content', 'Stillingsprosenten må være et tall.');
                } else if ($percent.val() < 0) {
                    $percent.parent().attr('data-content', 'Stillingsprosenten kan ikke være negativ.');
                } else if ($percent.val() > 100) {
                    $percent.parent().attr('data-content', 'Stillingsprosenten kan ikke være over 100.');
                }
                formError = true;
             }

            if(!formError){
                console.log("Submitting employee");
                formData = {
                    "firstName": $first.val(),
                    "lastName": $last.val(),
                    "email": $email.val(),
                    "phoneNumber": $phone.val(),
                    "category": $category.val(),
                    "workPercentage": parseFloat($percent.val()) / 100
                };
                console.log(JSON.stringify(formData));
                submitUser(formData);
            }
        }
    });
});

function submitUser(formData) {
    $.ajax({
        url: "/rest/admin/createuser",
        type: 'POST',
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify(formData),
        success: addUser,
        error: invalidField
    });
}

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
    createSuccess = true;
    console.log("OK", data);
    console.log("Adduser");
    $('.title').text("Vellykket!");
    $('.result').text("Bruker ble laget med passord: " + data.password);

    //go to submitted users profile
    $('#userViewBtn').click(function() {
        window.location="user-a.html";
    });

    $('.popup').show();
}

function invalidField(data){
    $('.title').text("Feil");

    if(data.responseJSON == null) {
        $('.result').text("En uventet feil oppsto");
    } else {
        $('.result').text(data.responseJSON.error);
    }

    $('#userViewBtn').hide();
    $('.popup').show();
}

//close popup
$('#userCloseBtn').click(function() {
    $('.popup').hide();

    if(createSuccess) {
        $('.register-form')[0].reset();
    }
    createSuccess = false;
});

//close popup when clicking outside of the popup
var $popup = $('#userPopup');
window.onclick = function(event) {
    if (event.target !== $popup) {
        $popup.hide();
        if(createSuccess) {
            $('.register-form')[0].reset();
        }
        createSuccess = false;
    }
};