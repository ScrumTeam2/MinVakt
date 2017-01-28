/**
 * Created by ingvildbroen on 20.01.2017.
 */

var $this, feedId, shiftId, categoryPop;
var popVisible = false;
var popOpened = false;

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

// Toggle messages
$('.container-title').click(function() {
    if (!popVisible) {
        var $this = $(this);
        $this.siblings('.feed-messages').toggle('1000');
        $this.children('div').children('.right-arrow-circle').toggleClass("rotate90");
    }
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
    hidePopup();
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
    hidePopup();
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
                showNotification(data[i]);
                notification++;
                break;
            default:
                console.log("Category not known", data[i].category);
        }
    }

    var $remove = $('.remove-message');
    $remove.on("click", function(e){
        if (!popVisible) {
            e.preventDefault();
            var element = $(e.currentTarget).parent();
            removeMessage(element);
        }
    });

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

//open popup
function openPopup(e){
    e.preventDefault();
    $this = $(e.currentTarget);
    feedId = $this.children().first().data("feed");
    shiftId = $this.children().first().data("shift");
    categoryPop = $this.children().first().data("cat");
    var content = $this.children().first().children().first().children().first().html();
    var $popup = $('#content');
    var $showPop = $('#feed-popup');

    switch(categoryPop){
        case "SHIFT_CHANGE_EMPLOYEE":
            $popup.html(
                `<h3>Vil du ta denne vakten?</h3>
                <p>${content}</p>`
            );
            $showPop.show();
            break;
        default:
            console.log("Category not known", categoryPop);
    }
}

// remove notification
function removeMessage(element){
    feedId = element.data("feed");
    setResolved(feedId);
}

function hidePopup(e){
    e.preventDefault();
    popVisible = false;
    popOpened = false;
    var $popup = $('.popup');
    $popup.hide();
}


// set boolean to true
function setResolved(feedId){
    var resolvedTo = true;
    resolveTask(feedId, resolvedTo);
}

// set boolean to false
function setUnResolved(feedId){
    var resolvedTo = false;
    resolveTask(feedId, resolvedTo);
}

// post resolved/unresolved
function resolveTask(feedId, resolvedTo){
    $.ajax({
        url: "/rest/newsfeed/" + feedId,
        type: 'POST',
        data: {
            accepted: resolvedTo
        },
        success: function(){
            console.log("ok post", feedId);
            loadMessages();
        },
        error: function(){
            console.log("ikke ok post", feedId);
        }
    });
}


// add request for shift messages
function acceptShift(data){
    var $requests = $('#requests');
    var content = data.content;
    var html=
        `<a href="#" id="open-popup">
            <div class="watch" data-feed="${data.feedId}" data-shift="${data.shiftId}" data-cat="${data.category}">
                <div class="watch-info">
                    <p class="lead">${content}</p>
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
}

//add notifications messages
function showNotification(data){
    var $notifications = $('#notifications');
    var content = data.content;
    var html=
        `<div class="watch" data-feed="${data.feedId}">
                <div class="watch-info">
                    <p class="lead">${content}</p>
                </div>
                <a href="#" class="remove-message">
                    <i class="material-icons">close</i>
                </a>
        </div>`;
    var $html = $(html);
    $notifications.append($html);
}

var $popup = $('.popup');
$(document).on("click", function (e) {
    if(popOpened){
        if (popVisible) {
            if (!$popup.is(e.currentTarget) && $popup.has(e.target).length === 0) {
                //hidePopup(e);
                popOpened = false;
                console.log("should hide");
            }
        }
    } else{
        console.log("open, set true");
        popOpened = true;
    }
});