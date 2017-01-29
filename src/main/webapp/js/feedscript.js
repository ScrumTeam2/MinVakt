/**
 * Created by marith 18.01.2017.
 */

var $this, feedId, categoryPop, shiftId, userId;
var popVisible = false;
var popOpened = false;

var shiftTypes = {"DAY" : "Dagvakt", "EVENING" : "Kveldsvakt", "NIGHT" : "Nattevakt"};

$(document).ready(function(){
    loadMessages();
});

function loadMessages(){
    $.ajax({
        url: "/rest/newsfeed",
        type: 'GET',
        success: showMessages,
        error: function(e) {
            console.error("Load feed", e);
        }
    });
}

function loadShiftInfo(feedData, callback){
    $.ajax({
        url: "/rest/shift/" + feedData.shiftId,
        type: 'GET',
        success: function(data){
            callback(data);
        }
    });
}

function loadDeptInfo(shiftData, deptCallback){
    $.ajax({
        url: "/rest/department/" + shiftData.deptId,
        type: 'GET',
        success: function(data){
            deptCallback(data);
        }
    });
}

// Toggle messages
$('.container-title').click(function() {
    if (!popVisible) {
        var $this = $(this);
        $this.siblings('.feed-messages').toggle('1000');
        $this.children('div').children('.right-arrow-circle').toggleClass("rotate90");
    }
});


// display messages sorted by category
function showMessages(data){
    var $changes = $('#accept_change');
    var $absence = $('#accept_absence');
    var $timebank = $('#accept_timebank');
    var $notifications = $('#show_notification');

    $changes.html("");
    $absence.html("");
    $timebank.html("");
    $notifications.html("");

    var change = 0;
    var absence = 0;
    var timebank = 0;
    var notification = 0;

    for(var i in data){
        switch(data[i].category){
            case "SHIFT_CHANGE_ADMIN":
                acceptChangeover(data[i]);
                change++;
                break;
            case "TIMEBANK":
                acceptTimebank(data[i]);
                timebank++;
                break;
            case "VALID_ABSENCE":
                acceptAbsence(data[i]);
                absence++;
                break;
            case "NOTIFICATION":
                adminNotification(data[i]);
                notification++;
                break;
            default:
                console.log("Category not known", data[i].category);
        }
    }

    //display category as empty if no messages
    var empty = `<div class="watch">
                <div class="watch-info">
                    <p class="lead greyed-out">Ingen meldinger</p>
                </div>
            </div>`;

    if(change === 0){
        $changes.html(empty);
    }
    if(timebank === 0){
        $timebank.html(empty);
    }
    if(absence === 0){
        $absence.html(empty);
    }
    if(notification === 0){
        $notifications.html(empty);
    }
}

//close button
var $close = $('#closePop');
$close.on("click", function(e){
    e.preventDefault();
    hidePopup(e);
});

//yes button
var $accept = $('#acceptBtn');
$accept.on("click", function(e){
    e.preventDefault();
    console.log("yes", feedId);
    $.ajax({
        url: "/rest/newsfeed/" + feedId,
        type: 'POST',
        contentType: "application/x-www-form-urlencoded",
        success: postOk,
        error: postNotOk
    });
    hidePopup(e);
});

//edit button
var $edit = $('#editBtn');
$edit.on("click", function(e){
    e.preventDefault();
    window.location = "/html/edit-shift.html?id=" + shiftId + "&feedId=" + feedId + "&userId=" + userId;
});

//no button
var $deny = $('#denyBtn');
$deny.on("click", function(e){
    e.preventDefault();
    console.log("no", feedId);
    var formData = {"accepted":false};

    $.ajax({
        url: "/rest/newsfeed/" + feedId,
        type: 'POST',
        contentType: "application/x-www-form-urlencoded",
        data: formData,
        success: postOk,
        error: postNotOk
    });
    hidePopup(e);
});

function showPopup(e){
    e.preventDefault();
    popVisible = true;
    var $popup = $('.popup');
    $popup.show();
}


function hidePopup(e){
    e.preventDefault();
    popVisible = false;
    popOpened = false;
    var $popup = $('.popup');
    $popup.hide();
}

//open popup
function openPopup(e){
    e.preventDefault();
    $this = $(e.currentTarget);
    feedId = $this.children().first().data("feed");
    categoryPop = $this.children().first().data("cat");
    shiftId = $this.children().first().data("shift");
    userId = $this.children().first().data("user");
    var content = $this.children().first().children().first().children().first().html();
    var shift = $this.children().first().children().first().children().last().html();
    var $popupCont = $('#content');

    var $yes = $('#acceptBtn');
    var $edit = $('#editBtn');

    if($yes.hasClass("hide")){
        $yes.removeClass("hide");
    }
    if($edit.hasClass("hide")){
        $edit.removeClass("hide");
    }

    switch(categoryPop){
        case "SHIFT_CHANGE_ADMIN":
            $yes.addClass("hide");
            $popupCont.html(
                `<h3>Godkjenne vaktbytte?</h3>
                <p>${content}<br>${shift}</p>
                <p>Det finnes ingen ledige ansatte i samme kategori</p>`
            );
            showPopup(e);
            break;
        case "TIMEBANK":
            $edit.addClass("hide");
            $popupCont.html(
                `<h3>Godkjenne timeavvik?</h3>
                <p>${content}<br>${shift}</p>`
            );
            showPopup(e);
            break;
        case "VALID_ABSENCE":
            $yes.addClass("hide");
            $popupCont.html(
                `<h3>Godkjenne fravær?</h3>
                <p>${content}<br>${shift}</p>`
            );
            showPopup(e);
            break;
        default:
            console.log("Category not known", categoryPop);
    }
}

// remove notification
function removeMessage(element){
    feedId = element.data("feed");
    console.log(feedId);
    $.ajax({
        url: "/rest/newsfeed/" + feedId,
        type: 'POST',
        contentType: "application/x-www-form-urlencoded",
        success: postOk,
        error: postNotOk
    });
}

function postOk(){
    console.log("ok post", feedId);
    loadMessages();
}

function postNotOk(){
    console.log("ikke ok post", feedId);
}

// add changeover messages
function acceptChangeover(feedData){
    var feedId = feedData.feedId;
    var category = feedData.category;
    var content = feedData.content;
    var shiftId = feedData.shiftId;
    var userId = feedData.userIdInvolving;
    loadShiftInfo(feedData, function(shiftData){
        var shiftType = shiftTypes[shiftData.type];
        var shiftDate = convertDate(shiftData.date);
        var $changes = $('#accept_change');
        var html=
            `<a href="#" id="open-popup" class="open-pop">
            <div class="watch" data-feed="${feedId}" data-cat="${category}" data-shift="${shiftId}" data-user="${userId}">
                <div class="watch-info">
                    <p class="lead">${content}</p>
                    <p class="sub">${shiftType}, ${shiftDate}</p>
                </div>
                <i class="symbol">
                    <i class="material-icons">chevron_right</i>
                </i>
            </div>
        </a>`;
        var $html = $(html);

        $changes.append($html);
        $html.on("click", function(e){
            if (!popVisible) {
                e.preventDefault();
                openPopup(e);
                popOpened = false;
            }
        });
    });
}

// add timebank messages
function acceptTimebank(feedData){
    var feedId = feedData.feedId;
    var category = feedData.category;
    var content = feedData.content;
    var shiftId = feedData.shiftId;
    loadShiftInfo(feedData, function(shiftData){
        var shiftType = shiftTypes[shiftData.type];
        var shiftDate = convertDate(shiftData.date);
        var $timebank = $('#accept_timebank');
        var html=
            `<a href="#" id="open-popup" class="open-pop">
            <div class="watch" data-feed="${feedId}" data-cat="${category}" data-shift="${shiftId}">
                <div class="watch-info">
                    <p class="lead">${content}</p>
                    <p class="sub">${shiftType}, ${shiftDate}</p>
                </div>
                <i class="symbol">
                    <i class="material-icons">chevron_right</i>
                </i>
            </div>
        </a>`;
        var $html = $(html);

        $timebank.append($html);
        $html.on("click", function(e){
            if (!popVisible) {
                e.preventDefault();
                openPopup(e);
                popOpened = false;
            }
        });
    });
}

// add absence messages
function acceptAbsence(feedData){
    var feedId = feedData.feedId;
    var category = feedData.category;
    var content = feedData.content;
    var shiftId = feedData.shiftId;
    var userId = feedData.userIdInvolving;

    loadShiftInfo(feedData, function(shiftData){
        var shiftType = shiftTypes[shiftData.type];
        var shiftDate = convertDate(shiftData.date);
        var $absence = $('#accept_absence');
        var html=
            `<a href="#" id="open-popup" class="open-pop">
            <div class="watch" data-feed="${feedId}" data-cat="${category}" data-shift="${shiftId}" data-user="${userId}">
                <div class="watch-info">
                    <p class="lead">${content}</p>
                    <p class="sub">${shiftType}, ${shiftDate}</p>
                </div>
                <i class="symbol">
                    <i class="material-icons">chevron_right</i>
                </i>
            </div>
        </a>`;
        var $html = $(html);

        $absence.append($html);
        $html.on("click", function(e){
            if (!popVisible) {
                e.preventDefault();
                openPopup(e);
                popOpened = false;
            }
        });
    });
}

//add notifications messages
function adminNotification(feedData){
    var feedId = feedData.feedId;
    var content = feedData.content;
    loadShiftInfo(feedData, function(shiftData){
        var shiftType = shiftTypes[shiftData.type];
        var shiftDate = convertDate(shiftData.date);
        loadDeptInfo(shiftData, function(deptData){
            var shiftDpt = deptData.name;
            var $notifications = $('#show_notification');
            var html=
                `<div class="watch" data-feed="${feedId}">
                    <div class="watch-info">
                        <p class="lead">${content}</p>
                        <p class="sub">${shiftDpt}: ${shiftType}, ${shiftDate}</p>
                    </div>
                </div>`;
            var $html = $(html);
            $notifications.append($html);
            var html2=
                `<a href="#" class="remove-message" id="remove">
                    <i class="material-icons">close</i>
                </a>`;
            var $html2 = $(html2);
            $html.append($html2);
            $html2.on("click", function(e){
                if (!popVisible) {
                    e.preventDefault();
                    var element = $(e.currentTarget).parent();
                    removeMessage(element);
                }
            });
        });
    });
}

//hide popup if click outside
var $popup = $('.popup');
$(document).on("click", function (e) {
    if(popOpened){
        if (popVisible) {
            if (!$popup.is(e.currentTarget) && $popup.has(e.target).length === 0) {
                hidePopup(e);
                popOpened = false;
            }
        }
    } else{
        popOpened = true;
    }
});