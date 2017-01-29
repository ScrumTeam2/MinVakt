package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.util.FormattingUtil;

/**
 * Created by Marit on 27.01.2017.
 */

//Contains all the newfeed "contentUtil"
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
     * @param user
     * @return
     */
    //NOTIFICATION
    public String userResponsible(User user){
        String res = "Ny ansvarsvakt: "+user.getFirstName()+" "+user.getLastName();
        return res;
    }

    /**
     * @param minutes
     * @return
     */
    //NOTIFICATION
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

    /**
     * @return
     */
    //NOTIFICATION
    public String rejectTimebank(){
        String res ="Overtid ikke godkjent";
        return res;
    }

    /**
     * @return
     */
    //NOTIFICATION
    public String acceptValidAbsence(){
        String res ="Fravær godkjent";
        return res;
    }

    /**
     * @return
     */
    //NOTIFICATION
    public String rejectValidAbsence(){
        String res ="Fravær ikke godkjent";
        return res;
    }

    /**
     * @return
     */
    //NOTIFICATION
    public String shiftChangeUserTo(){
        String res ="Du er satt på en ny vakt";
        return res;
    }

    /**
     * @return
     */
    //NOTIFICATION
    public String shiftChangeUserFrom(){
        String res = "Ønske om vaktbytte godkjent";
        return res;
    }

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
