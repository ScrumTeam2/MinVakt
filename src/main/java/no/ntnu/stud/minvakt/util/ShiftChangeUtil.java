package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.*;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shift.ShiftUser;
import no.ntnu.stud.minvakt.data.user.User;
import static no.ntnu.stud.minvakt.data.NewsFeedItem.NewsFeedCategory.*;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;
import no.ntnu.stud.minvakt.database.OvertimeDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;

import javax.management.Notification;
import java.sql.Time;
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
    static OvertimeDBManager overtimeDB = new OvertimeDBManager();

    public boolean findNewUserToShift(int shiftId, int userId){
       // boolean statusOk = shiftDB.deleteEmployeeFromShift(userId,shiftId, false);
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
        return false;
    }
    public static boolean updateNotification(int feedId, boolean shiftAccepted){
        NewsFeedItem newsFeedItem = newsDB.getNewsFeedItem(feedId);
        switch (newsFeedItem.getCategory()) {
            case SHIFT_CHANGE_ADMIN:
                return approveShiftChangeAdmin(newsFeedItem, shiftAccepted);
            case SHIFT_CHANGE_EMPLOYEE:
                return approveShiftChangeEmployee(newsFeedItem, shiftAccepted);
            case VALID_ABSENCE:
                return approveValidAbsence(newsFeedItem,shiftAccepted);
            case NOTIFICATION:
                return newsDB.setNewsFeedItemResolved(feedId, shiftAccepted);
            case TIMEBANK:
                return approveTimeBank(newsFeedItem, shiftAccepted);
            default:
                return false;
        }

    }
    private static boolean approveTimeBank(NewsFeedItem newsFeedItem, boolean shiftAccepted){
        if(shiftAccepted){
            if(overtimeDB.approveOvertime(newsFeedItem.getUserIdInvolving(), newsFeedItem.getShiftId())){

                Timestamp timestamp = Timestamp.from(Instant.now());
                Shift shift = shiftDB.getShift(newsFeedItem.getShiftId());
                if(!overtimeDB.approveOvertime(newsFeedItem.getUserIdInvolving(), newsFeedItem.getShiftId())) return false;
                NewsFeedItem notification = new NewsFeedItem(-1, timestamp, "Din overtid på vakten den "+shift.getDate()+ " er blitt godkjent av" +
                        " administrasjonen.", newsFeedItem.getUserIdInvolving(), newsFeedItem.getUserIdTo(),
                        newsFeedItem.getShiftId(), TIMEBANK);
                newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
                return newsDB.createNotification(notification) != 0;
            }
            else return false;
        }
        else {
            Timestamp timestamp = Timestamp.from(Instant.now());
            Shift shift = shiftDB.getShift(newsFeedItem.getShiftId());
            NewsFeedItem notification = new NewsFeedItem(-1, timestamp, "Din overtid på vakten den " + shift.getDate() + " er ikke blitt godkjent av" +
                    " administrasjonen!", newsFeedItem.getUserIdInvolving(), newsFeedItem.getUserIdTo(),
                    newsFeedItem.getShiftId(), TIMEBANK);
            overtimeDB.deleteOvertime(newsFeedItem.getUserIdInvolving(), newsFeedItem.getShiftId(), newsFeedItem.getStartTimeTimebank());
            newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
            return newsDB.createNotification(notification) != 0;
        }
    }

    private static boolean approveValidAbsence(NewsFeedItem newsFeedItem, boolean shiftAccepted){
        if(shiftAccepted){
            if(!newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), shiftAccepted) ||
            !shiftDB.setValidAbsence(newsFeedItem.getUserIdInvolving(), newsFeedItem.getShiftId(), true)) return false;
        }
        newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
        return true;
    }
    private static boolean approveShiftChangeEmployee(NewsFeedItem newsFeedItem, boolean shiftAccepted){
        if(shiftAccepted){

            //Get data needed to create notification
            User userAccepted = userDB.getUserById(newsFeedItem.getUserIdTo());
            User userInvolving = userDB.getUserById(newsFeedItem.getUserIdInvolving());
            Shift shift = shiftDB.getShift(newsFeedItem.getShiftId());
            Timestamp timestamp = Timestamp.from(Instant.now());

            //Get an admin ID to attach to the notification
            int adminId = userDB.getAdminId();
            if(adminId == 0) return false;

            //Create a notification to be sent to admin.
            NewsFeedItem notification = new NewsFeedItem(-1, timestamp, userAccepted.getFirstName()+" "+userAccepted.getLastName()+" ønsker å ta vakten " +
                    "til "+userInvolving.getFirstName()+" "+userInvolving.getLastName()+" på dato "+shift.getDate(), adminId, newsFeedItem.getUserIdTo(),
                    newsFeedItem.getShiftId(), SHIFT_CHANGE_ADMIN);
            int status =  newsDB.createNotification(notification);
            if(status == 0) return false;
        }
        //Remove current newsFeedItem
        return newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
    }
    private static boolean approveShiftChangeAdmin(NewsFeedItem newsFeedItem, boolean shiftAccepted){
        if(shiftAccepted) {
            User userFrom = userDB.getUserById(newsFeedItem.getUserIdTo());
            User userTo = userDB.getUserById(newsFeedItem.getUserIdTo());
            ShiftUser shiftUser = shiftDB.getUserFromShift(userTo.getId(), newsFeedItem.getShiftId());

            //Removes old user and adds new user to shift, if something goes wrong, returns false.
            if (!shiftDB.deleteEmployeeFromShift(userFrom.getId(), newsFeedItem.getShiftId())||
                    !shiftDB.addEmployeeToShift(shiftUser, newsFeedItem.getShiftId())) return false;

            //Sets news feed items resolved
            newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);

            Shift shift = shiftDB.getShift(newsFeedItem.getShiftId());

            //Creates new update notification to the user who wants to change shift.
            NewsFeedItem notification = new NewsFeedItem(-1, Timestamp.from(Instant.now()),
                    "Din vakt den " + shift.getDate() + " er byttet bort til " + userTo.getFirstName() + " " + userTo.getLastName()
                            + ".", userTo.getId(), userFrom.getId(), shift.getId(), NOTIFICATION);
            newsDB.createNotification(notification);

            //Creates update notification for user who accepted the shift change
            NewsFeedItem notification2 = new NewsFeedItem(-1, Timestamp.from(Instant.now()),
                    "Dit vaktbytte den " + shift.getDate() + " er godkjent av administrator!",
                    userFrom.getId(), userTo.getId(), shift.getId(), NOTIFICATION);
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
