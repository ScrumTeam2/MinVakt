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
    $.ajax({
        url: "/rest/department",
        type: 'GET',
        success: addDepartments,
        error: function (data) {
            console.log("Not able to get userdata")
        }
    });
    setEventListeners();
});
function fillUserData(data){
    console.log(data);
    console.log(data.firstName);

    var $firstname = $("#firstname");
    var $lastname = $("#lastname");
    var $email = $("#email");
    var $phone = $("#phone");
    var $percentage = $("#percentage");
    var categoryOptions = $("#category").children();

    $firstname.val(data.firstName);
    $lastname.val(data.lastName);
    $email.val(data.email);
    $phone.val(data.phoneNumber);
    $percentage.val(data.workPercentage * 100);
    for(var i = 0; i < categoryOptions.length;i++){
        if($(categoryOptions[i]).attr("value") == data.category){
            $(categoryOptions[i]).prop("selected", true);
        }
    }
    $.ajax({
        url: "/rest/department",
        type: 'GET',
        success: function (depts) {
            var element = $("#department");
            $.each(depts, function (index, dept) {
                var html = "";
                if(dept.id == data.deptId) {
                    html += "<option selected>"+dept.name+"</option>";
                }
                else{
                    html += "<option>"+dept.name+"</option>";
                }
                element.append(html);
            });
        },
        error: function (data) {
            console.log("Not able to get departments")
        }
    });
}
function setEventListeners() {
    var cancelButton = $("#change-user-button");
    var changeUserButton = $("#cancel-change-button");
    cancelButton.click(location.reload());
    changeUserButton.click(function () {
        e.preventDefault();
        var formError = false;
        if (!$first.val()) {
            $first.addClass('error').parent().attr('data-content', 'Du må fylle inn fornavn.');
            formError = true;
        }
        if (!$last.val()) {
            $last.addClass('error').parent().attr('data-content', 'Du må fylle inn etternavn.');
            formError = true;
        }
        if (!$email.val() || !(/[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/.test($email.val()))) {
            $email.addClass('error');
            if (!$email.val()) {
                $email.parent().attr('data-content', 'Du må fylle inn e-post.');
            } else {
                $email.parent().attr('data-content', 'E-posten du skrev inn er ikke gyldig.');
            }
            formError = true;
        }
        if (!$phone.val() || !(/^[0-9]{8,8}$/.test($phone.val()))) {
            $phone.addClass('error');
            if (!$phone.val()) {
                $phone.parent().attr('data-content', 'Du må fylle inn mobilnummer.');
            } else {
                $phone.parent().attr('data-content', 'Mobilnummeret du skrev inn er ikke gyldig.');
            }
            formError = true;
        }

        var formData;

        console.log(formError);
        console.log(userValue);

        if (userValue === "admin") {
            if (!formError) {
                console.log("Submitting admin");
                formData = {
                    "firstName": $first.val(),
                    "lastName": $last.val(),
                    "email": $email.val(),
                    "phoneNumber": $phone.val(),
                    "category": 'ADMIN',
                    "workPercentage": 1,
                    "deptId": 1
                };
                submitUser(formData);
            }

        } else {

            if (!$category.val()) {
                $category.addClass('error').parent().attr('data-content', 'Du må velge en kategori.');
                formError = true;
            }

            if (!$department.val()) {
                $department.addClass('error').parent().attr('data-content', 'Du må velge en avdeling.');
                formError = true;
            }

            if (!$percent.val() || isNaN($percent.val()) || $percent.val() < 0 || $percent.val() > 100) {
                $percent.addClass('error');
                if (!$percent.val()) {
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

            if (!formError) {
                console.log("Submitting employee");
                formData = {
                    "firstName": $first.val(),
                    "lastName": $last.val(),
                    "email": $email.val(),
                    "phoneNumber": $phone.val(),
                    "category": $category.val(),
                    "workPercentage": parseFloat($percent.val()) / 100,
                    "deptId": $department.val()
                };
                console.log(JSON.stringify(formData));
                submitUser(formData);
            }
        }
    });
    initPopup();
    }
    }
}
var createSuccess = false;
var $first;
var $last;
var $percent;
var $email;
var $phone;
var $category;
var $department;

$(document).ready(function () {
    $first = $('#firstname');
    $last = $('#lastname');
    $percent = $('#percentage');
    $email = $('#email');
    $phone = $('#phone');
    $category = $('#category');
    loadDepartments();


    var userValue = $(".user-type:checked").val();

    $(".user-type").on('change', function () {
        userValue = $(".user-type:checked").val();

        if (userValue === "admin") {
            showAdminInput();
        } else {
            showEmployeeInput();
        }
    });


    $('#userBtn').click(function (e) {

});

function initPopup() {
    //close popup
    $('#userCloseBtn').click(function () {
        $('.popup').hide();

        if (createSuccess) {
            $('.register-form')[0].reset();
        }
        createSuccess = false;
    });

    //close popup when clicking outside of the popup
    var $popup = $('#userPopup');
    window.onclick = function (e) {
        if (!$popup.is(e.target) && $popup.has(e.target).length === 0) {
            $popup.hide();
            if (createSuccess) {
                $('.register-form')[0].reset();
            }
            createSuccess = false;
        }
    };
}

function submitUser(formData) {
    // Loading animation
    $('#userBtn').html(`<div class="typing_loader"></div>`);

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

function showAdminInput() {
    var $select = $('.select-container');
    var $percent = $('#percentage');
    $select.addClass("hide");
    $percent.addClass("hide");
    $department.addClass("hide");
}


function showEmployeeInput() {
    var $select = $('.select-container');
    var $percent = $('#percentage');
    $select.removeClass("hide");
    $percent.removeClass("hide");
    $department.removeClass("hide");
}

function addUser(data) {
    createSuccess = true;
    console.log("OK", data);
    console.log("Adduser");
    $('.title').text("Vellykket!");
    $('.result').text("Bruker ble laget med passord: " + data.password);
    $('#userViewBtn').attr("href", "user-a.html?search=" + $first.val() + " " + $last.val());

    $('.popup').show();

    // Reset loading animation
    $('#userBtn').text("Registrer bruker");
}

function invalidField(data) {
    $('.title').text("Feil");

    if (data.responseJSON == null) {
        $('.result').text("En uventet feil oppsto");
    } else {
        $('.result').text(data.responseJSON.error);
    }

    $('#userViewBtn').hide();
    $('.popup').show();

    // Reset loading animation
    $('#userBtn').text("Registrer bruker");
}

function loadDepartments() {
    $department = $('#department');

    $.get("/rest/department/")
        .done(function (data) {
            data.forEach(function (department) {
                $department.append(`<option value="${department.id}">${department.name}</option>`);
            })
        })
        .fail(function (data) {
            console.log("fail", data);
        });
}