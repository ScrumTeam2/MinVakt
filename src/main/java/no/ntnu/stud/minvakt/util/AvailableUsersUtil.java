package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.UserBasicWorkHours;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;
import no.ntnu.stud.minvakt.database.OvertimeDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Marit on 13.01.2017.
 */

public class AvailableUsersUtil {

    public ArrayList<UserBasicWorkHours> sortAvailableEmployees(int shiftId, Date date){
        AvailabilityDBManager availDBManager = new AvailabilityDBManager();
        OvertimeDBManager overtimeDBManager = new OvertimeDBManager();
        ShiftDBManager shiftDBManager = new ShiftDBManager();

        //Finds first and last day of week to calculate total workhours for 1 week
        Calendar c = Calendar.getInstance();
        c.setTime(date);

        c.set(Calendar.DAY_OF_WEEK, 1);
        Date firstDayOfWeek = c.getTime();
        java.sql.Date sqlFirstDay = new java.sql.Date(firstDayOfWeek.getTime());

        c.set(Calendar.DAY_OF_WEEK, 7);
        Date lastDayOfWeek = c.getTime();
        java.sql.Date sqlLastDay = new java.sql.Date(lastDayOfWeek.getTime());

        //Fetches available employees for a shift
        ArrayList<UserBasicWorkHours> userList = availDBManager.getAvailabilityUserBasic(shiftId);

        //Fetches workhours from DB
        for (UserBasicWorkHours user : userList) {
            user.setShiftHours(overtimeDBManager.getOvertimeHours(user.getId(), sqlFirstDay, sqlLastDay));
            user.setOvertime(shiftDBManager.getShiftHours(user.getId(), sqlFirstDay, sqlLastDay));
            user.calculateTotalWorkHours();
        }

        //Sorts list of employees by workhours, ascending order
        userList.sort(UserBasicWorkHours.workhoursComparator);
        return userList;
    }
}