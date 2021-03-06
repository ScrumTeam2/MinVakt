package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.shift.Shift;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * An utility class to format/convert emun values
 */
public class FormattingUtil {
    private static HashMap <Integer,String> monthNames;
    private static HashMap<Integer, String> weekNames;


    /** formatting shift type/time of day to norwegian string
     * @param shiftType - the shift type/time of day
     * @return formatted string
     */
    public static String formatShiftType(Shift.ShiftType shiftType){
        if (shiftType.getValue() == 0){
            return "dagvakt";
        }else if(shiftType.getValue() == 1){
            return "kveldsvakt";
        }else{
            return "nattvakt";
        }


    }

    /**Formatting date to string
     * @param date - unformatted sql date
     * @return string date as nameOfWeekday + dayOfMonth + nameOfMonth
     */
    public static String formatDate(Date date){

        if(monthNames == null) {
            monthNames = new HashMap<>(12);
            monthNames.put(Calendar.JANUARY, "januar");
            monthNames.put(Calendar.FEBRUARY, "februar");
            monthNames.put(Calendar.MARCH, "mars");
            monthNames.put(Calendar.APRIL, "april");
            monthNames.put(Calendar.MAY, "mai");
            monthNames.put(Calendar.JUNE, "juni");
            monthNames.put(Calendar.JULY, "juli");
            monthNames.put(Calendar.AUGUST, "august");
            monthNames.put(Calendar.SEPTEMBER, "september");
            monthNames.put(Calendar.OCTOBER, "oktober");
            monthNames.put(Calendar.NOVEMBER, "november");
            monthNames.put(Calendar.DECEMBER, "desember");

        }
        if(weekNames == null){
            weekNames = new HashMap<>(7);
            weekNames.put(Calendar.SUNDAY,"søndag");
            weekNames.put(Calendar.MONDAY,"mandag");
            weekNames.put(Calendar.TUESDAY,"tirsdag");
            weekNames.put(Calendar.WEDNESDAY,"onsdag");
            weekNames.put(Calendar.THURSDAY,"torsdag");
            weekNames.put(Calendar.FRIDAY,"fredag");
            weekNames.put(Calendar.SATURDAY,"lørdag");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        String day = weekNames.get(cal.get(Calendar.DAY_OF_WEEK));
        String month = monthNames.get(cal.get(Calendar.MONTH));
        //  String year = Integer.toString(cal.get(Calendar.YEAR));
        String dayOfMonth = Integer.toString(cal.get(Calendar.DAY_OF_MONTH));
        return day+" "+dayOfMonth+". "+month;
    }
}
