$(document).ready(function() {
    loadCalendar();
});

function loadCalendar() {
    //Hente dato trykket på
    var tableId = $('#calendar-availability tr');
    var columnId = $('#calendar-availability tr td');
    var cells = document.getElementsByTagName('td');
    for (var i = 0; i < cells.length; i++) {
        cells[i].addEventListener('click', clickHandler);
    }
}

var C = function Calendar(month, year) {
    var now = new Date();

    // labels for week days and months
    var day  = 1,
        prev = 1,
        next = 1;
    var dayInWeek = ['Man', 'Tir', 'Ons', 'Tor', 'Fre', 'Lør', 'Søn'],
        monthInYear = ['Januar', 'Februar', 'Mars', 'April', 'Mai', 'Juni', 'Juli', 'August', 'September', 'Oktober', 'November', 'Desember'];

    //Check if date (month) is correct
    if(isNaN(month) || month == null) {
        this.month = now.getMonth() + 1;
    } else {
        this.month = month;
    }
    //Check if date (year) is correct
    if(isNaN(year) || year == null) {
        this.year = now.getFullYear();
    } else {
        this.year = year;
    }
    var nowMonth = this.month - 1;

    var dayStartMonth = new Date(this.year, nowMonth, 1),
        dayStartWeek = dayStartMonth.getDay() == 0 ? 7 : dayStartMonth.getDay();

    var monthEndDay = new Date(this.year, this.month, 0).getDate(),
        prevMonthEndDay = new Date(this.year, nowMonth, 0).getDate();

    var dateStartString = this.year + "-" + this.month + "-01";
    //Get calendar data
    $.ajax({
        url: "../rest/shift",
        data: {daysForward : 31, date : dateStartString},
        type: 'GET',
        dataType: 'json',
        success: function (data) {
            console.log(data);
            //Set header
            var html = '<div class="calendar-header"> <a href="#" class="prev"> <i onclick="switchDate(0);" class="material-icons">chevron_left</i> </a>';
            html += '<h3>' + monthInYear[nowMonth] + ' ' + this.year + '</h3>';
            html += '<a href="#" class="next"> <i onclick="switchDate(1);"class="material-icons">chevron_right</i> </a> </div>';
            //Populate fields
            html += '<table class="calendar-table">';

            html += '<thead>';
            html += '<tr class="weekdays">';
            for (i = 0; i <= 6; i++) {
                html += '<th class="day">';
                html += dayInWeek[i];
                html += '</th>';
            }
            html += '</tr>';
            html += '</thead>';

            html += '<tbody>';
            html += '<tr class="week">';
            // weeks loop (rows)
            for (i = 0; i < 9; i++) {
                // weekdays loop (cells)
                for (var j = 1; j <= 7; j++) {
                    if (day <= monthEndDay && (i > 0 || j >= dayStartWeek)) {
                        // current month
                        html += '<td class="day">';
                        html += day;
                        html += '</td>';
                        day++;
                    } else {
                        if (day <= monthEndDay) {
                            // previous month
                            html += '<td class="day disabled-month">';
                            html += prevMonthEndDay - dayStartWeek + prev + 1;
                            html += '</td>';
                            prev++;
                        } else {
                            // next month
                            html += '<td class="day disabled-month">';
                            html += next;
                            html += '</td>';
                            next++;
                        }
                    }
                }

                // stop making rows if it's the end of month
                if (day > monthEndDay) {
                    html += '</tr>';
                    break;
                } else {
                    html += '</tr><tr class="week">';
                }
            }
            html += '</tbody>';
            html += '</table>';

            return html;
        },
        error: function (data) {
            //console.log("Error, no data found");
            var calendarList = $(".list");
            calendarList.append("<p>" + data + "</p>");
        }
    });

};

// document.getElementById('calendar').innerHTML = Calendar(12, 2015); 
document.getElementById('calendar').innerHTML = C();

C.prototype.switchDate = function(postfix) {
    var curMonth = this.month;
    var curYear = this.year;
    var num = 0;
    if(postfix == 0) {
        this.month--;
    } else {
        this.month++;
    }
    document.getElementById('calendar').innerHTML = C(this.month,this.year);

}

function clickHandler() {
    var url;
    console.log(this.textContent);
    console.log(month);
    var dateString = year + '-' + month + '-' + this.textContent;
    console.log(dateString);
    if (this.textContent > 0) {
        if ($(".person").length > 0)
            url = "/rest/shift/user";
        else {
            url = "/rest/shift";
        }
        $.ajax({
            url: url,
            type: 'GET',
            datatype: 'json',
            data: {date: dateString},
            success: createUserShiftHtml,
            error: invalid
        });
    }
}

function success(data) {
    console.log(data);
    if(data.length <1) {
        $("#error").html( "<p>Ingen ledige vakter på denne datoen</p>").fadeIn(1500);
        setTimeout(function(){  $("#error").fadeOut(1000);}, 1000);
    }
    console.log('success send data');

}
function invalid(data) {

}

function switchDate(postfix) {
    var switched = false;
    if(this.month == 12 && postfix>0) {
        this.month = 1;
        this.year++;
        switched = true;
    }
    if(this.month == 1 && postfix<1) {
        this.month = 12;
        this.year--;
        switched = true;
    }
    var num = 0;
    if(postfix == 0 && !switched) {
        this.month--;
    } else {
        if(!switched) {
            this.month++;
        }
    }
    document.getElementById('calendar').innerHTML = C(this.month,this.year);
    loadCalendar();
}