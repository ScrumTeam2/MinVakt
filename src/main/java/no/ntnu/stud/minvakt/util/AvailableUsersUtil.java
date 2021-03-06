package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasicWorkHours;
import no.ntnu.stud.minvakt.database.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.sql.Timestamp;

/**
 * Controller class for returning various info about available users
 */
public class AvailableUsersUtil {
    private static final int SHIFT_LENGTH_MINUTES = 60*8;   //Using 40 hours of work/week
    private static final int MAX_WORK_MINUTES = 32*60+1;         //If the employee has more than 32 hours of work, they can't accept a new 8 hour shift
    FormattingUtil format = new FormattingUtil();
    NewsFeedDBManager newsFeedDMB = new NewsFeedDBManager();
    UserDBManager userDBM = new UserDBManager();
    ContentUtil contentUtil = new ContentUtil();

    /**Sorts available employees on a shift by workhours
     * @param shiftId - the shift that needs a new employee
     * @param date - the date of the shift
     * @return arraylist with sorted employees by category as UserBasicWorkHours objects
     */
    public static ArrayList<UserBasicWorkHours> sortAvailableEmployees(int shiftId, LocalDate date){
        AvailabilityDBManager availDBManager = new AvailabilityDBManager();
        OvertimeDBManager overtimeDBManager = new OvertimeDBManager();
        ShiftDBManager shiftDBManager = new ShiftDBManager();

        //Finds first and last day of week to calculate total workhours for 1 week
        LocalDate firstDayOfWeek = date.with(DayOfWeek.MONDAY);
        LocalDate lastDayOfWeek = date.with(DayOfWeek.SUNDAY);

        java.sql.Date sqlFirstDay = java.sql.Date.valueOf(firstDayOfWeek);
        java.sql.Date sqlLastDay = java.sql.Date.valueOf(lastDayOfWeek);

        //Fetches available employees for a shift
        ArrayList<UserBasicWorkHours> userList = availDBManager.getAvailabilityUserBasic(shiftId);
        ArrayList<UserBasicWorkHours> userListDelimited = new ArrayList<>();

        //Fetches workhours from DB
        for (UserBasicWorkHours user : userList) {
            user.setOvertime(overtimeDBManager.getMinutesByDate(user.getId(), sqlFirstDay, sqlLastDay));
            user.setShiftMinutes(SHIFT_LENGTH_MINUTES * shiftDBManager.getNumberOfShifts(user.getId(), sqlFirstDay, sqlLastDay));
            user.calculateTotalWorkHours();
            //If the user doesn't have too many work hours they are put in the new list

            if(user.getTotalWorkMinutes() < MAX_WORK_MINUTES){
                userListDelimited.add(user);
            }
        }
        //Sorts list of employees by workhours, ascending order
        userListDelimited.sort(UserBasicWorkHours.workHoursComparator);
        return userListDelimited;
    }

    /**Sorts available employees on a shift by workhours and category, then returns the ones with the given category first
     * @param shiftId - the shift that needs a new employee
     * @param date - the date of the shift
     * @param category - the employee category
     * @param onlyThisCategory - if True, returns the list with only employees in the given category
     * @return arraylist with sorted employees by category as UserBasicWorkHours objects
     */
     public ArrayList<UserBasicWorkHours> sortAvailableEmployeesWithCategory(int shiftId, LocalDate date, User.UserCategory category, boolean onlyThisCategory){
        ArrayList<UserBasicWorkHours> sortedEmployees = AvailableUsersUtil.sortAvailableEmployees(shiftId, date);
        ArrayList<UserBasicWorkHours> outputEmployees = new ArrayList<>();
        for (UserBasicWorkHours user : sortedEmployees) {
            if (user.getCategory() == category) {
                outputEmployees.add(user);
            }
        }
        if(!onlyThisCategory){
            for (UserBasicWorkHours user : sortedEmployees) {
                if (user.getCategory() != category) {
                    outputEmployees.add(user);
                }
            }
        }
        return outputEmployees;
    }

    /**Finds available users for a shift and sends a notification to the qualified users
     * @param shift - shift ID for the shift that needs a new employee
     * @param userFrom - the employee that originally was working on this shift
     * @param dateTime - the timestamp for the notification
     * @return True if successful
     */
     public boolean sendNotificationOfShiftChange(Shift shift, User userFrom, Timestamp dateTime){
        User.UserCategory category = userFrom.getCategory();

        ArrayList<UserBasicWorkHours> userList = sortAvailableEmployeesWithCategory(
                shift.getId(), shift.getDate().toLocalDate(), category, true);
        boolean ok;

        //If user list is not empty, send notifications to employees on list
        if(!userList.isEmpty()){
            for(UserBasicWorkHours userTo : userList){

                NewsFeedItem notification = new NewsFeedItem(-1, dateTime,
                        contentUtil.employeeShiftChange(shift), userTo.getId(), userFrom.getId(),
                        shift.getId(), NewsFeedItem.NewsFeedCategory.SHIFT_CHANGE_EMPLOYEE);
                int status =  newsFeedDMB.createNotification(notification);

                if (status ==0){
                    System.out.println("could not make notification for user "+userTo.getId());
                }
            }
            ok = true;
        }
        //if user list is empty, send notification to administrator
        else{
            int adminId = userDBM.getAdminId();
            if(adminId == 0) {
                System.out.println("Could not find admin user");
                return false;
            }
            NewsFeedItem notification = new NewsFeedItem(-1, dateTime,
                    contentUtil.shiftChangeAdmin(userFrom), adminId,userFrom.getId(),
                    shift.getId(), NewsFeedItem.NewsFeedCategory.SHIFT_CHANGE_ADMIN);
            int status =  newsFeedDMB.createNotification(notification);

            if (status ==0){
                System.out.println("Could not make notification to administrator");
                return false;
            }
            ok = true;
        }
        return ok;
    }
}