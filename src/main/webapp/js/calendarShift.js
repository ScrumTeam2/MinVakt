$(document).ready(function() {
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
var dateClicked = new Date();
var C = function Calendar(month, year, data) {

    if(!this.data){
        this.data = {};
    }
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
    var shiftDate;
    var hasUser = false;
    var isAvailable = false;
    var count = 0;
    var timeOfDay;
    var currentDate;
    console.log(data);
    for (i = 0; i < 9; i++) {
        // weekdays loop (cells)
        for (var j = 1; j <= 7; j++) {
            if (day <= monthEndDay && (i > 0 || j >= dayStartWeek)) {
                // current month
                if(dateClicked && dateClicked.getDate() === day && dateClicked.getMonth() === nowMonth
                    && dateClicked.getFullYear() === this.year)
                    if(now.getDate() == day && now.getMonth() == nowMonth
                        && now.getFullYear() == this.year) {
                        html += '<td class="day current-day calendar-clicked">';
                    }
                    else    html += '<td class="day calendar-clicked">';
                else{
                    if (now.getDate() == day && now.getMonth() == nowMonth
                        && now.getFullYear() == this.year) {
                        html += '<td class="day current-day">';
                    }
                    else html += '<td class="day">';
                }
                //Add dots if person is on shift
                if(data[count]){
                    currentDate = new Date(data[count].date);
                    while(data[count] && currentDate.getDate() == day && currentDate.getMonth() == nowMonth){
                        if(data[count].available && !data[count].hasUser){
                            isAvailable = true;
                        }
                        if(data[count].hasUser){
                            hasUser = true;
                        }
                        count++;
                        if(data[count]) {
                            currentDate = new Date(data[count].date);
                        }
                    }
                }
                html += day;
                html += "<div class='circle-wrap'>";
                if(hasUser){
                    html += "<span class='circle blue'></span>";
                }
                if(isAvailable){
                    html += "<span class='circle green'></span>";
                }
                html += '</div></td>';
                day++;
                isAvailable = false;
                hasUser = false;
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
};

// document.getElementById('calendar').innerHTML = Calendar(12, 2015);
createCalendatWithData();

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

};

function clickHandler() {
    console.log("clickhandler");
    if($(this).hasClass("disabled-month")){
        return;
    }
    var url, data, dateString;
    console.log(this.textContent);
    console.log(month);
    dateString = year + '-' + month + '-' + this.textContent;
    console.log(dateString);
    if (this.textContent > 0) {
        data = {date:dateString, daysForward : 7};
        $.ajax({
            url: "/rest/shift",
            type: 'GET',
            datatype: 'json',
            data: data,
            success: createAllShiftsHtml,
            error: invalid
        });
        $(".calendar-clicked").removeClass("calendar-clicked");
        $(this).addClass("calendar-clicked");
        dateClicked = new Date();
        dateClicked.setFullYear(year);
        dateClicked.setDate(this.textContent);
        dateClicked.setMonth(month-1);

    }
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
    createCalendatWithData(this.month,this.year);
}
function createCalendatWithData(month,year) {
    //Check if date (month) is correct
    var now = new Date();
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
    var dateString = this.year + "-" + this.month + "-01";
    var thisMonth = this.month;
    var thisYear = this.year;
    $.ajax({
        url: "../rest/shift/",
        type: 'GET',
        dataType: 'json',
        data: {daysForward:31, date:dateString},
        success: function (data) {
            document.getElementById('calendar').innerHTML = C(thisMonth, thisYear, data);
            loadCalendar();

        },
        error: function (data) {
            console.log(data)
        }
    });
}