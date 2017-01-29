/**
 * Created by ingvildbroen on 20.01.2017.
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
    var $shifts = $('#requests');
    var $notifications = $('#notifications');

    $shifts.html("");
    $notifications.html("");

    var shift = 0;
    var notification = 0;
    for(var i in data){
        switch(data[i].category){
            case "SHIFT_CHANGE_EMPLOYEE":
                acceptShift(data[i]);
                shift++;
                break;
            case "NOTIFICATION":
                employeeNotification(data[i]);
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

    if(shift === 0){
        $shifts.html(empty);
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
    var $popup = $('#content');

    switch(categoryPop){
        case "SHIFT_CHANGE_EMPLOYEE":
            $popup.html(
                `<h3>Vil du ta denne vakten?</h3>
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

// add request for shift messages
function acceptShift(feedData){
    var feedId = feedData.feedId;
    var category = feedData.category;
    var content = feedData.content;
    var shiftId = feedData.shiftId;
    var userId = feedData.userIdInvolving;
    loadShiftInfo(feedData, function(shiftData){
        var shiftDpt = shiftData.deptId;
        var shiftDate = convertDate(shiftData.date);
        loadDeptInfo(shiftData, function(deptData){
            var shiftDpt = deptData.name;
            var $requests = $('#requests');
            var html=
                `<a href="#" id="open-popup">
                    <div class="watch" data-feed="${feedId}" data-shift="${shiftId}" data-cat="${category}" data-user="${userId}">
                        <div class="watch-info">
                            <p class="lead">${content}</p>
                            <p class="sub">${shiftDpt}, ${shiftDate}</p>
                        </div>
                        <i class="symbol right-arrow">
                            <i class="material-icons">chevron_right</i>
                        </i>
                    </div>
                </a>`;
            var $html = $(html);
            $requests.append($html);
            $html.on("click", function(e){
                if (!popVisible) {
                    e.preventDefault();
                    openPopup(e);
                    popOpened = false;
                }
            });
        });
    });
}

//add notifications messages
function employeeNotification(feedData){
    var feedId = feedData.feedId;
    var content = feedData.content;
    loadShiftInfo(feedData, function(shiftData){
        var $notifications = $('#notifications');
        var shiftType = shiftTypes[shiftData.type];
        var shiftDate = convertDate(shiftData.date);
        var html=
            `<div class="watch" data-feed="${feedId}">
                <div class="watch-info">
                    <p class="lead">${content}</p>
                    <p class="sub">${shiftType}, ${shiftDate}</p>
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
}

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