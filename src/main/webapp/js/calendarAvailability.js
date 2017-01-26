$(document).ready(function() {
    loadCalendar();
    createCalendarListener();
    loadAvailableShiftsChosen();
});
var shiftsChosen = [];

function loadCalendar() {
    //Hente dato trykket på
    var tableId = $('#calendar-availability tr');
    var columnId = $('#calendar-availability tr td');
    var cells = document.getElementsByTagName('td');
    for (var i = 0; i < cells.length; i++) {
        cells[i].addEventListener('click', clickHandler);
    }
}

function loadAvailableShiftsChosen() {
     $.ajax({
        url: "/rest/availability/user",
        type: 'GET',
        dataType: 'json',
        success: getAvailabilityForUser,
        error: function (data) {
            console.log(data);
            console.log('Error when loading available shifts chosen');
        }
    });
}

function getAvailabilityForUser(data) {
  console.log('got availability for user');
  console.log(data.shifts);
  shiftsChosen = data.shifts;
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

  //Set header
  var html = '<div class="calendar-header"> <a href="#" class="prev"> <i onclick="switchDate(0);" class="material-icons">chevron_left</i> </a>';
  html += '<h3>' + monthInYear[nowMonth] + ' ' + this.year + '</h3>';
  html += '<a href="#" class="next"> <i onclick="switchDate(1);"class="material-icons">chevron_right</i> </a> </div>';
  //Populate fields
  html += '<table class="calendar-table">';

  html += '<thead>';
  html += '<tr class="weekdays">';
  for (var i = 0; i <= 6; i++) {
    html += '<th class="day">';
    html += dayInWeek[i];
    html += '</th>';
  }
  html += '</tr>';
  html += '</thead>';

  html += '<tbody>';
  html += '<tr class="week">';
  // weeks loop (rows)
  for (var i = 0; i < 9; i++) {
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
}

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
/*
<div style="background-color:red;border-radius: 50%;width: 10px;height: 10px;margin: 0 auto;">
  </div>
  */
  var dateString = "";
function clickHandler() {
    console.log(this.textContent);
    console.log(month);
    /*$('.day').css("background-color", "");
    $(this).css("background-color", "#89e087");
    */
    console.log(this);
    if ($(this).hasClass('disabled-month')) {
      return;
    }

    console.log('thissssssssssssssss'); 
    $('.day').removeClass("daySelected");
    $(this).addClass("daySelected");
    dateString = year+'-'+month+'-'+this.textContent;
    console.log(dateString);
    if (this.textContent > 0) {
        $.ajax({
            url: "/rest/availability/date?date="+dateString,
            type: 'GET',
            //datatype: 'json',
           /* data: {
                dateSelection: dateString
            },*/
            success: success,
            error: invalid
        });
    }
}

function convertDate(dateInput){
    var monthNames = [
        "januar", "februar",
        "mars", "april", "mai",
        "juni", "juli", "august",
        "september", "oktober", "november",
        "desember"];

    var dayNames = ["Søndag", "Mandag", "Tirsdag",
        "Onsdag", "Torsdag", "Fredag",
        "Lørdag"];
    var date = new Date(dateInput);
    var day = date.getDate();
    var monthIndex = date.getMonth();
    var dayIndex = date.getDay();

      console.log('date input: ' + dateInput);
      console.log(day);
      console.log('day above');
    return dayNames[dayIndex] + " " + day + ". " + monthNames[monthIndex];
}

function success(data) {
 // console.log(data);
  if(data.length <1) {
   // $("#error").html( "<p>Ingen vakter på denne datoen. Du har enten valgt dato bak i tid, eller for langt fram i tid. Sistnevtne krever at admin registrerer ny turnus.</p>").fadeIn(400);
    //setTimeout(function(){  $("#error").fadeOut(400);}, 300);
    var calendarList = $(".list");
    calendarList.html("");
              html =
                  "<div class='container-title'>" +
                  "<h3>"+convertDate(dateString)+"</h3>" +
                  "</div>";
               html +=
              "<div class='watch'>" +
                      "<p class='lead'>Det finnes ingen vakter på denne datoen</p>" +
                  "</div>";
                  calendarList.append(html);
                  html ="";
  } else {
    displayAvailabilityHtml(data);
  }
  console.log('success send data');

}
function invalid(data) {

}

function displayAvailabilityHtml(data) {
  var calendarList = $(".list");
  calendarList.slideDown();
  calendarList.html("");
  var currentDate = "";
  var shiftTypes = {"DAY" : "Dagvakt", "EVENING" : "Kveldsvakt", "NIGHT" : "Nattevakt"};
  var shiftTimes = {"DAY" : "07.00 - 15.00", "EVENING" : "15.00 - 23.00", "NIGHT" : "23.00 - 07.00"}; 
  var shiftsAlreadyAvailable = [];
  for(var i=0; i<data.length; i++) {
    console.log('------shiftsChosen-----');
    console.log(shiftsChosen);
    console.log('------shiftsChosen-----');
    if(shiftsChosen.indexOf(data[i].shiftId)>-1) {
      console.log('already exists for : ' + data[i].shiftId);
      shiftsAlreadyAvailable.push(data[i].shiftId);
    } 
  }
  $.each(data, function (index, element) {
          console.log(shiftTypes[element.shiftType]);
          if(element.date !== currentDate){
              currentDate = element.date;
              html =
                  "<div class='container-title'>" +
                  "<h3>"+convertDate(element.date)+"</h3>" +
                  "</div>";
          }
          if(shiftsAlreadyAvailable.indexOf(element.shiftId) === -1) {
          html +=
              "<div class='watch'>" +
                  "<div class='watch-info'>" +
                      "<p class='lead'>"+shiftTypes[element.shiftType]+"</p>" +
                      "<p class='sub'>"+shiftTimes[element.shiftType]+"<br></p>" +
                      "<p class='sub'>"+element.deptName+"</p>" +
                  "</div>";
                  if(element.hasUser) {
                    html+="<p>Min vakt</p>";
                  } else {
                    html+="<input type='checkbox' class='checkBox' name='check' id='"+element.shiftId+"' value='employee'>"+
                    "<label for='"+element.shiftId+"'>Ledig</label>";
                  }
              html+="</div>";
        } else {
          console.log('shift exists. Display different html');
          html +=
              "<div class='watch'>" +
                  "<div class='watch-info'>" +
                      "<p class='lead'>"+shiftTypes[element.shiftType]+"</p>" +
                      "<p class='sub'>"+shiftTimes[element.shiftType]+"<br></p>" +
                      "<p class='sub'>"+element.deptName+"</p>"+
                  "</div>";
                  if(element.hasUser) {
                    html+="<p>Min vakt</p>";
                  } else {
                    html+="<input type='checkbox' class='checkBox2' name='check' id='"+element.shiftId+"' value='employee' checked>"+
                    "<label for='"+element.shiftId+"'>Du er satt som ledig</label>";
                  }
              html+="</div>";
        }
        calendarList.append(html);
        html = "";
      });
}

/*
var values = (function() {
                var a = [];
                $(".checkboxes:checked").each(function() {
                    a.push(this.value);
                });
                return a;
            })()
*/

$('#submitAvailability').click(function() {
  var selected = [];
  var unSelected = [];
  //Sett som ledig
  $('.checkBox:checkbox:checked').each(function() {
      console.log('checked from the otehr');
      var push = {id: $(this).attr('id')};
      selected.push(push);
      shiftsChosen.push(parseInt($(this).attr('id')));
  });
  //Sett som ikke ledig
  $('.checkBox2:checkbox:not(:checked)').each(function() {
      var push = {id: $(this).attr('id')};
      console.log($(this).attr('id'));
      unSelected.push(push);
      var index = shiftsChosen.indexOf(parseInt($(this).attr('id')));
      shiftsChosen.splice(index,1);
  });

  $.ajax({
      url: "/rest/availability/setAvailable",
      type: 'POST',
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify(selected),
      success: sentList,
      error: invalidList
  });
    $.ajax({
      url: "/rest/availability/deleteAvailable",
      type: 'POST',
      dataType: "json",
      contentType: "application/json",
      data: JSON.stringify(unSelected),
      success: sentList,
      error: invalidList
  });

  var calendarList = $(".list");
  calendarList.slideUp();
});

function sentList(data) {
}
function invalidList(data) {
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
function createCalendarListener(){
    var calendarIcon = $("#today");
    calendarIcon.click(function () {
        var calendar = $("#calendar");
        calendar.slideToggle();
    });
}