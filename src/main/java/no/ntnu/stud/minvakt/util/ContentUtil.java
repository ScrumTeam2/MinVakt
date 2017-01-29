package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.util.FormattingUtil;

/**
 * Handles generation of all news feed messages
 */
public class ContentUtil {
    public ContentUtil(){}

    /**Used for shiftChange content in newsfeed for employees
     * @param shift - ID of the available shift
     * @return formatted string with shifttype (day/evening/night)
     */
    public String employeeShiftChange(Shift shift){
        String res = "Ledig "+FormattingUtil.formatShiftType(shift.getType());
        return res;
    }

    /**Used for shiftChange notifications for administrators
     * @param userInvolving - the employee that wants to change the shift
     * @return formatted string with the name of the employee who wants to change his/her shift
     */
    public String shiftChangeAdmin(User userInvolving){
        String res  = userInvolving.getFirstName()+" "+userInvolving.getLastName()+" ønsker å bytte vakt";
        return res;
    }

    /**Used for valid absence notifications for administrators
     * @param user - the user who has registered valid absence (illness)
     * @return formatted string with the name of the employee who has registered valid absence
     */
    public String validAbsence(User user){
        String res = user.getFirstName()+" "+user.getLastName()+" har meldt fravær";
        return res;
    }

    /**Used for the "timebank" notification for administrators
     * @param user - employee who has registered overtime
     * @param minutes - #minutes the employee has worked
     * @return formatted string with name of the employee who has worked overtime and amt of minutes
     */
    public String regTimebank(User user, int minutes){
        String res = user.getFirstName()+" "+user.getLastName()+" har registert overtid "+minutesFormat(minutes);
        return res;
    }

    /**TODO: remove method?
     * @param shift
     * @param userAccepted
     * @param userInvolving
     * @return
     */
    public String shiftChangeAdminUserFromTo(Shift shift, User userAccepted, User userInvolving){
        String res  = userAccepted.getFirstName()+" "+userAccepted.getLastName()+" ønsker å ta vakten " +
                "til "+userInvolving.getFirstName()+" "+userInvolving.getLastName()+" på dato "+
                FormattingUtil.formatDate(shift.getDate());
        return res;
    }

    /** Notification for the administrator that there has been selected a new employee as responsible for a shift
     * @param user employee who is responsible
     * @return formatted string with user name
     */
    public String userResponsible(User user){
        String res = "Ny ansvarsvakt: "+user.getFirstName()+" "+user.getLastName();
        return res;
    }

    /** notification for overtime/absence
     * @param minutes - the amount of minutes the employee has worked extra/been absent
     * @return  if negative minutes, returns string formatted for absence
     *          if postive minutes, returns string formatted for overtime
     */
    public String acceptTimebank(int minutes){
        String res;
        if(minutes>0){
            res ="Overtid registrert: "+minutesFormat(minutes);
        }else {
            int posMin = Math.abs(minutes);
            res ="Fravær registrert : "+minutesFormat(posMin);
        }
        return res;
    }

    /** notification if overtime is not accepted
     * @return string with "rejected" notification
     */
    public String rejectTimebank(){
        String res ="Overtid ikke godkjent";
        return res;
    }

    /** notification absence accepted
     * @return string with "accepted" notification
     */
    public String acceptValidAbsence(){
        String res ="Fravær godkjent";
        return res;
    }

    /** notification valid absence not accepted (illness, for an entire shift)
     * @return string with "absence not accepted"
     */
    public String rejectValidAbsence(){
        String res ="Fravær ikke godkjent";
        return res;
    }

    /** notification about new shift
     * @return string with new shift notification
     */
    public String shiftChangeUserTo(){
        String res ="Du er satt på en ny vakt";
        return res;
    }

    /** notification about shift change acceptance
     * @return string with shift change accepted notification
     */
    //NOTIFICATION
    public String shiftChangeUserFrom(){
        String res = "Ønske om vaktbytte godkjent";
        return res;
    }
    public String shiftChangeUserFromNotAccepted(){
        String res = "Ønske om vaktbytte ikke godkjent";
        return res;
    }

    /** private method to format minutes to minutes + hours
     * @param minutes minutes that needs formatting
     * @return  if minutes<60, string with just minutes
     *          if minutes>=60, string with hours and minutes
     */
    private String minutesFormat(int minutes){
        String res;
        if(minutes<60){
            res = "("+minutes+"min)";
        }else{
            int hours = minutes/60;
            int minRest = minutes%60;
            res = "("+hours+"t "+minRest+"min)";
        }
        return res;
    }
}
