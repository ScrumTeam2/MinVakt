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

    //SHIFT_CHANGE_EMPLOYEE
    public String employeeShiftChange(Shift shift){
        String res = "Ledig "+FormattingUtil.formatShiftType(shift.getType());
        return res;
    }

    //SHIFT_CHANGE_ADMIN
    public String shiftChangeAdmin(User userInvolving){
        String res  = userInvolving.getFirstName()+" "+userInvolving.getLastName()+" ønsker å bytte vakt";
        return res;
    }

    //VALID_ABSENCE
    public String validAbsence(User user){
        String res = user.getFirstName()+" "+user.getLastName()+" har meldt fravær";
        return res;
    }

    //TIMEBANK
    public String regTimebank(User user, int minutes){
        String res = user.getFirstName()+" "+user.getLastName()+" har registert overtid "+minutesFormat(minutes);
        return res;
    }

    //SHIFT_CHANGE_ADMIN
    public String shiftChangeAdminUserFromTo(Shift shift, User userAccepted, User userInvolving){
        String res  = userAccepted.getFirstName()+" "+userAccepted.getLastName()+" ønsker å ta vakten " +
                "til "+userInvolving.getFirstName()+" "+userInvolving.getLastName()+" på dato "+
                FormattingUtil.formatDate(shift.getDate());
        return res;
    }

    //NOTIFICATION
    public String userResponsible(User user){
        String res = "Ny ansvarsvakt: "+user.getFirstName()+" "+user.getLastName();
        return res;
    }

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

    //NOTIFICATION
    public String rejectTimebank(){
        String res ="Overtid ikke godkjent";
        return res;
    }

    //NOTIFICATION
    public String acceptValidAbsence(){
        String res ="Fravær godkjent";
        return res;
    }

    //NOTIFICATION
    public String rejectValidAbsence(){
        String res ="Fravær ikke godkjent";
        return res;
    }

    //NOTIFICATION
    public String shiftChangeUserTo(){
        String res ="Du er satt på en ny vakt";
        return res;
    }

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
