/**
 * Created by marith 18.01.2017.
 */

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
    var $this = $(this);
    $this.siblings('.feed-messages').toggle('1000');
    $this.children('div').children('.right-arrow-circle').toggleClass("rotate90");
});

var $this, feedId, shiftId, categoryPop;

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
                showNotification(data[i]);
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
        case "SHIFT_CHANGE_ADMIN":
            $popup.html(
                `<h3>Godkjenne vaktbytte?</h3>
                <p>${content}</p>`
            );
            $showPop.show();
            break;
        case "TIMEBANK":
            $popup.html(
                `<h3>Godkjenne timebank?</h3>
                <p>${content}</p>`
            );
            $showPop.show();
            break;
        case "VALID_ABSENCE":
            $popup.html(
                `<h3>Godkjenne fravær?</h3>
                <p>${content}</p>`
            );
            $showPop.show();
            break;
        case "NOTIFICATION":
            // removes directly
            setResolved(feedId);
            break;
        default:
            console.log("Category not known", categoryPop);
    }
}

//no button
var $deny = $('#denyBtn');
$deny.on("click", function(e, feedId){
    e.preventDefault();
    setUnResolved(feedId);
    closePopup(e, feedId);
});

//yes button
var $accept = $('#acceptBtn');
$accept.on("click", function(e){
    e.preventDefault();
    setResolved(feedId);
    closePopup(e, feedId);
});

//closes popup after pressing yes or no
function closePopup(e, feedId){
    e.preventDefault();
    $('#feed-popup').hide();
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


// add changeover messages
function acceptChangeover(data){
    var $changes = $('#accept_change');
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
    $changes.append($html);
    $html.on("click", function(e){
        openPopup(e);
    });
}

// add absence messages
function acceptAbsence(data){
    var $absence = $('#accept_absence');
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
    $absence.append($html);
    $html.on("click", function(e){
        openPopup(e);
    });
}

// add timebank messages
function acceptTimebank(data){
    var $timebank = $('#accept_timebank');
    var content = data.content;
    var html=
        `<a href="#" id="open-popup">
            <div class="watch" data-feed="${data.feedId}" data-cat="${data.category}">
                <div class="watch-info">
                    <p class="lead">${content}</p>
                </div>
                <i class="symbol right-arrow">
                    <i class="material-icons">chevron_right</i>
                </i>
            </div>
        </a>`;
    var $html = $(html);
    $timebank.append($html);
    $html.on("click", function(e){
        openPopup(e);
    });
}

//add notifications messages
function showNotification(data){
    var $notifications = $('#show_notification');
    var content = data.content;
    var html=
        `<a href="#" id="open-popup">
            <div class="watch" data-feed="${data.feedId}" data-cat="${data.category}">
                <div class="watch-info">
                    <p class="lead">${content}</p>
                </div>
                <i class="material-icons">close</i>
            </div>
        </a>`;
    var $html = $(html);
    $notifications.append($html);
    $html.on("click", function(e){
        openPopup(e);
    });
}