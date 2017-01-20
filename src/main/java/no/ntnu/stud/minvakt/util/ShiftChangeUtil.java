package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.data.UserBasicWorkHours;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by evend on 1/20/2017.
 */
public class ShiftChangeUtil {
    public static boolean findNewUserToShift(int shiftId, int userId){
        ShiftDBManager shiftDB = new ShiftDBManager();
        UserDBManager userDB = new UserDBManager();
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

}
