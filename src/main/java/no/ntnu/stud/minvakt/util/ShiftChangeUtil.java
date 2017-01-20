package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.*;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by evend on 1/20/2017.
 */
public class ShiftChangeUtil {
    ShiftDBManager shiftDB = new ShiftDBManager();
    UserDBManager userDB = new UserDBManager();
    NewsFeedDBManager newsDB = new NewsFeedDBManager();

    public boolean findNewUserToShift(int shiftId, int userId){
        boolean statusOk = shiftDB.deleteEmployeeFromShift(userId,shiftId);
        /*if(statusOk) {
            User deletedUser = userDB.getUserById(userId);
            Shift shift = shiftDB.getShift(shiftId);
            ArrayList<UserBasicWorkHours> users = AvailableUsersUtil.sortAvailableEmployeesWithCategory(Shift shift, User deletedUser);
            if(users.isEmpty()){
                sendNotificationToAdmin();
            }
            else if(users.get(0).getCategory() == deletedUser.getCategory()){
                sendApprovalToAdmin(users)
            }
            else{
                sendNotificationToUsers(users)
            }
        }*/
        return statusOk;
    }
    public boolean approveShiftChange(int feedId){
        NewsFeedItem newsFeedItem = newsDB.getNewsFeedItem(feedId);
        User user = userDB.getUserById(newsFeedItem.getUserIdTo());
        ShiftUser shiftUser = shiftDB.getUserFromShift(user.getId(), newsFeedItem.getShiftId());
        //shiftDB.addEmployeeToShift();
        return false;
    }

}
