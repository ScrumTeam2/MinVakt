package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.*;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shift.ShiftUser;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by evend on 1/20/2017.
 */
public class ShiftChangeUtil {
    static ShiftDBManager shiftDB = new ShiftDBManager();
    static UserDBManager userDB = new UserDBManager();
    static NewsFeedDBManager newsDB = new NewsFeedDBManager();

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
    public static boolean updateNotification(int feedId, boolean shiftAccepted){
        NewsFeedItem newsFeedItem = newsDB.getNewsFeedItem(feedId);
        if(newsFeedItem.getCategory() == NewsFeedItem.NewsFeedCategory.SHIFT_CHANGE){
            return approveShiftChange(newsFeedItem, shiftAccepted);
        }
        else if(newsFeedItem.getCategory() == NewsFeedItem.NewsFeedCategory.NOTIFICATION){
            return newsDB.setNewsFeedItemResolved(feedId, shiftAccepted);
        }

        return false;
    }
    private static boolean approveShiftChange(NewsFeedItem newsFeedItem, boolean shiftAccepted){
        if(shiftAccepted) {
            User userFrom = userDB.getUserById(newsFeedItem.getUserIdTo());
            User userTo = userDB.getUserById(newsFeedItem.getUserIdTo());
            ShiftUser shiftUser = shiftDB.getUserFromShift(userTo.getId(), newsFeedItem.getShiftId());

            //Removes old user and adds new user to shift, if something goes wrong, returns false.
            if (!shiftDB.addEmployeeToShift(shiftUser, newsFeedItem.getShiftId())
                    || !shiftDB.deleteEmployeeFromShift(userFrom.getId(), newsFeedItem.getShiftId())) return false;

            //Sets news feed items resolved
            newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);

            Shift shift = shiftDB.getShift(newsFeedItem.getShiftId());

            //Creates new update notification to the user who wants to change shift.
            NewsFeedItem notification = new NewsFeedItem(-1, Timestamp.from(Instant.now()),
                    "Din vakt den " + shift.getDate() + " er byttet bort til " + userTo.getFirstName() + " " + userTo.getLastName()
                            + ".", userTo.getId(), userFrom.getId(), shift.getId(), NewsFeedItem.NewsFeedCategory.NOTIFICATION);
            newsDB.createNotification(notification);

            //Creates update notification for user who accepted the shift change
            NewsFeedItem notification2 = new NewsFeedItem(-1, Timestamp.from(Instant.now()),
                    "Dit vaktbytte den " + shift.getDate() + " er godkjent av administrator!",
                    userFrom.getId(), userTo.getId(), shift.getId(), NewsFeedItem.NewsFeedCategory.NOTIFICATION);
            newsDB.createNotification(notification);
            newsDB.createNotification(notification2);
            return true;
        }
        else {
            //Removes admin notification if not accepted
            return newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
        }

    }

}
