$(document).ready(function () {
    $.ajax({
        url: "../rest/profile",
        type: 'GET',
        dataType: 'json',
        success: buildProfileHtml,
        error: function (data) {
            var calendarList = $(".list");
            calendarList.append("<p>" + data + "</p>");
        }
    });

});