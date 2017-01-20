package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.data.UserBasic;
import no.ntnu.stud.minvakt.data.UserBasicWorkHours;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;
import no.ntnu.stud.minvakt.database.OvertimeDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Marit on 13.01.2017.
 */

public class AvailableUsersUtil {

    public static ArrayList<UserBasicWorkHours> sortAvailableEmployees(int shiftId, Date date){
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
            user.setOvertime(overtimeDBManager.getOvertimeHours(user.getId(), sqlFirstDay, sqlLastDay));
            user.setShiftHours(shiftDBManager.getShiftHours(user.getId(), sqlFirstDay, sqlLastDay));
            user.calculateTotalWorkHours();
        }

        //Sorts list of employees by workhours, ascending order
        userList.sort(UserBasicWorkHours.workHoursComparator);
        return userList;
    }

    public static ArrayList<UserBasicWorkHours> sortAvailableEmployeesWithCategory(
            Date date, int shiftId, User.UserCategory category){
        ArrayList<UserBasicWorkHours> sortedEmployees = AvailableUsersUtil.sortAvailableEmployees(shiftId, date);
        ArrayList<UserBasicWorkHours> outputEmployees = new ArrayList<>();
        for (UserBasicWorkHours user : sortedEmployees) {
            if (user.getCategory() == category) {
                outputEmployees.add(user);
            }
        }
        for (UserBasicWorkHours user : sortedEmployees) {
            if (user.getCategory() != category) {
                outputEmployees.add(user);
            }
        }
        return outputEmployees;
    }
    public static ArrayList<UserBasicWorkHours> sortAvailableEmployeesIgnoreAvailability(Date date, int limit){
        OvertimeDBManager overtimeDBManager = new OvertimeDBManager();
        ShiftDBManager shiftDBManager = new ShiftDBManager();

        LocalDate localDate = TimeUtil.convertJavaDate(date);
        //Finds first and last day of week to calculate total workhours for 1 week
        LocalDate firstDayThisWeek = localDate.with(DayOfWeek.MONDAY);
        LocalDate lastDayThisWeek = localDate.with(DayOfWeek.SUNDAY);

        java.sql.Date sqlFirstDay = java.sql.Date.valueOf(firstDayThisWeek);
        java.sql.Date sqlLastDay = java.sql.Date.valueOf(lastDayThisWeek);

        //Fetches available employees for a shift
        ArrayList<UserBasicWorkHours> userList = shiftDBManager.getOrdinaryWorkHoursForPeriod(sqlFirstDay, sqlLastDay, limit);

        //Fetches workhours from DB
        for (UserBasicWorkHours user : userList) {
            user.setOvertime(overtimeDBManager.getOvertimeHours(user.getId(), sqlFirstDay, sqlLastDay));
            user.calculateTotalWorkHours();
        }

        //Sorts list of employees by workhours, ascending order
        userList.sort(UserBasicWorkHours.workHoursComparator);
        return userList;
    }
}