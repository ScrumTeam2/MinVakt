package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasicWorkHours;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;
import no.ntnu.stud.minvakt.database.OvertimeDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Marit on 13.01.2017.
 */

public class AvailableUsersUtil {
    private static final int SHIFT_LENGTH_MINUTES = 60*8;

    public static ArrayList<UserBasicWorkHours> sortAvailableEmployees(int shiftId, LocalDate date){
        AvailabilityDBManager availDBManager = new AvailabilityDBManager();
        OvertimeDBManager overtimeDBManager = new OvertimeDBManager();
        ShiftDBManager shiftDBManager = new ShiftDBManager();

        //Finds first and last day of week to calculate total workhours for 1 week
        LocalDate firstDayOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate lastDayOfWeek = date.with(DayOfWeek.SUNDAY);

        java.sql.Date sqlFirstDay = java.sql.Date.valueOf(firstDayOfWeek);
        java.sql.Date sqlLastDay = java.sql.Date.valueOf(lastDayOfWeek);
        //System.out.println("Dato: "+ date);
        //System.out.println("Fra dato: "+ sqlFirstDay);
        //System.out.println("Til dato: "+ sqlLastDay);

        //Fetches available employees for a shift
        ArrayList<UserBasicWorkHours> userList = availDBManager.getAvailabilityUserBasic(shiftId);


        //Fetches workhours from DB
        for (UserBasicWorkHours user : userList) {
            user.setOvertime(overtimeDBManager.getMinutes(user.getId(), sqlFirstDay, sqlLastDay));
            user.setShiftMinutes(SHIFT_LENGTH_MINUTES*shiftDBManager.getNumberOfShifts(user.getId(), sqlFirstDay, sqlLastDay));
            user.calculateTotalWorkHours();
        }

        //Sorts list of employees by workhours, ascending order
        userList.sort(UserBasicWorkHours.workHoursComparator);
        return userList;
    }

    public static ArrayList<UserBasicWorkHours> sortAvailableEmployeesWithCategory(LocalDate date, int shiftId, User.UserCategory category){
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

//    public ArrayList<UserBasicWorkHours> sortAvailableEmployeesIgnoreAvailability(LocalDate startDate, int limit) {
//        ShiftDBManager shiftDBManager = new ShiftDBManager();
//        OvertimeDBManager overtimeDBManager = new OvertimeDBManager();
//
//        LocalDate lastDate = startDate.plusWeeks(6);
//
//        java.sql.Date sqlFirstDay = java.sql.Date.valueOf(startDate);
//        java.sql.Date sqlLastDay = java.sql.Date.valueOf(lastDate);
//
//        //Fetches available employees for a shift
//        ArrayList<UserBasicWorkHours> userList = shiftDBManager.getOrdinaryWorkHoursForPeriod(sqlFirstDay, sqlLastDay, limit);
//
//
//        for (UserBasicWorkHours user : userList) {
//
//            user.setOvertime(overtimeDBManager.getMinutes(user.getId(), sqlFirstDay, sqlLastDay));
//            user.setShiftMinutes(SHIFT_LENGTH_MINUTES*shiftDBManager.getNumberOfShifts(user.getId(), sqlFirstDay, sqlLastDay));
//            user.calculateTotalWorkHours();
//        }
//
//        //Sorts list of employees by workhours, ascending order
//        userList.sort(UserBasicWorkHours.workHoursComparator);
//        return userList;
//    }
}