/**
 * Created by evend on 1/12/2017.
 */
$.ajax({
    url: "rest/shift/user/"+userId,
    type: 'GET',
    dataType: 'json',
    success: createUserShiftHtml(data),
    error: invalid
});

function createUserShiftHtml(data) {
    $.each(data, function (element) {
        
    })
}
