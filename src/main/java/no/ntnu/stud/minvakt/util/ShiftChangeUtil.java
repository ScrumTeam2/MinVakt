package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shift.ShiftUser;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;
import no.ntnu.stud.minvakt.database.OvertimeDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;

import static no.ntnu.stud.minvakt.data.NewsFeedItem.NewsFeedCategory.*;


/**
 * Created by evend on 1/20/2017.
 */
public class ShiftChangeUtil {
    private static ShiftDBManager shiftDB = new ShiftDBManager();
    private static UserDBManager userDB = new UserDBManager();
    private static NewsFeedDBManager newsDB = new NewsFeedDBManager();
    private static OvertimeDBManager overtimeDB = new OvertimeDBManager();
    private static ContentUtil contentUtil = new ContentUtil();

    /** sends notification to admin about new responsibe employee on shift
     * @param userId - the ID of the employee who set as responsible
     * @param shiftId - the ID of the shift
     * @return True if successful
     */
    public static boolean sendNewResponsibleChangeNotification(int userId, int shiftId){
        User user = userDB.getUserById(userId);
        Timestamp timestamp = Timestamp.from(Instant.now());
        int adminId = userDB.getAdminId();
        if(adminId == 0) return false;
        NewsFeedItem newsFeedItem = new NewsFeedItem(-1,timestamp,
                contentUtil.userResponsible(user), adminId,userId,shiftId, NewsFeedItem.NewsFeedCategory.NOTIFICATION);
        return newsDB.createNotification(newsFeedItem) != 0;
    }

    /** updates news feed notification given by ID, according to which news feed category the news feed item is.
     * @param feedId - the ID of the news feed notification to be updated
     * @param shiftAccepted - boolean used by cases where something is to be accepted or declined, default true if not applicable
     * @return True if successful
     */
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

    /** Accepts or rejects overtime registered by employee, and resolves notification
     * @param newsFeedItem - the ID news feed notification
     * @param shiftAccepted - boolean if the overtime is to be accepted (true) or not (false)
     * @return True if successfull
     */
    private static boolean approveTimeBank(NewsFeedItem newsFeedItem, boolean shiftAccepted){
        if(shiftAccepted){
            if(overtimeDB.approveOvertime(newsFeedItem.getUserIdInvolving(), newsFeedItem.getShiftId())){
                Timestamp timestamp = Timestamp.from(Instant.now());
                if(!overtimeDB.approveOvertime(newsFeedItem.getUserIdInvolving(), newsFeedItem.getShiftId())) return false;
                System.out.println(newsFeedItem);
                int minutes = overtimeDB.getMinutes(newsFeedItem.getUserIdInvolving(), newsFeedItem.getShiftId(), newsFeedItem.getStartTimeTimebank());
                System.out.println(minutes);
                NewsFeedItem notification = new NewsFeedItem(-1, timestamp,
                        contentUtil.acceptTimebank(minutes), newsFeedItem.getUserIdInvolving(), newsFeedItem.getUserIdInvolving(),
                        newsFeedItem.getShiftId(), NOTIFICATION);

                newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
                return newsDB.createNotification(notification) != 0;
            }
            else return false;
        }
        else {
            Timestamp timestamp = Timestamp.from(Instant.now());
            NewsFeedItem notification = new NewsFeedItem(-1, timestamp,
                    contentUtil.rejectTimebank(), newsFeedItem.getUserIdInvolving(), newsFeedItem.getUserIdInvolving(),
                    newsFeedItem.getShiftId(), NOTIFICATION);
            overtimeDB.deleteOvertime(newsFeedItem.getUserIdInvolving(), newsFeedItem.getShiftId(), newsFeedItem.getStartTimeTimebank());
            newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
            return newsDB.createNotification(notification) != 0;
        }
    }

    /** approves valid absence (ex. illness) that an employee has registered
     * @param newsFeedItem - the ID of the news feed notification
     * @param shiftAccepted - boolean if absence is accepted or not
     * @return True if successfull
     */
    private static boolean approveValidAbsence(NewsFeedItem newsFeedItem, boolean shiftAccepted){
        if(shiftAccepted){
            if(!newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), shiftAccepted) ||
            !shiftDB.setValidAbsence(newsFeedItem.getUserIdInvolving(), newsFeedItem.getShiftId(), 2)) return false;
            Shift shift = shiftDB.getShift(newsFeedItem.getShiftId());
            NewsFeedItem notification = new NewsFeedItem(-1, Timestamp.from(Instant.now()),
                    contentUtil.acceptValidAbsence(), newsFeedItem.getUserIdInvolving(),
                    newsFeedItem.getUserIdInvolving(), shift.getId(), NOTIFICATION);

            newsDB.createNotification(notification);
            boolean result = newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
            return result;
        }
        else {
            Shift shift = shiftDB.getShift(newsFeedItem.getShiftId());
            NewsFeedItem notification = new NewsFeedItem(-1, Timestamp.from(Instant.now()),
                    contentUtil.rejectValidAbsence(), newsFeedItem.getUserIdInvolving(),
                    newsFeedItem.getUserIdInvolving(), shift.getId(), NOTIFICATION);

            newsDB.createNotification(notification);
            return newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
        }

    }

    /** Employee accepts or rejects an offered shift (where they have set themselves as available).
     * If rejected: method checks if there are other employees who have not
     * accepted/rejected the new shift. If there are none pending, method sends notification to admin
     * @param newsFeedItem - the ID of the news feed notification
     * @param shiftAccepted - boolean if shift is accepted or not
     * @return True if succesfully made notification
     */
    private static boolean approveShiftChangeEmployee(NewsFeedItem newsFeedItem, boolean shiftAccepted){
        //Get data needed to create notification
        Timestamp timestamp = Timestamp.from(Instant.now());
        Shift shift = shiftDB.getShift(newsFeedItem.getShiftId());
        User userAccepted = userDB.getUserById(newsFeedItem.getUserIdTo());
        User userInvolving = userDB.getUserById(newsFeedItem.getUserIdInvolving());
        boolean statusNewsfeed = true;

        if(shiftAccepted){
            if (userAccepted == null || userInvolving == null) return false;
            //ShiftUser shiftUser = shiftDB.getUserFromShift(userTo.getId(), newsFeedItem.getShiftId());
            //(int userId, String userName, User.UserCategory userCategory, boolean responsibility, int valid_absence, int departmentId)
            String userName = userAccepted.getFirstName() + " " + userAccepted.getLastName();
            ShiftUser shiftUser = new ShiftUser(userAccepted.getId(), userName, userAccepted.getCategory(),
                    false, 0, userAccepted.getDeptId());
            System.out.println(shiftUser);

            //Removes old user and adds new user to shift, if something goes wrong, returns false.
            if (!shiftDB.deleteEmployeeFromShift(userInvolving.getId(), newsFeedItem.getShiftId()) ||
                    !shiftDB.addEmployeeToShift(shiftUser, newsFeedItem.getShiftId())) return false;
            //Sets news feed items resolved
            if(!newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true)) return false;

            //Creates new update notification to the user who wants to change shift.
            NewsFeedItem notification = new NewsFeedItem(-1, Timestamp.from(Instant.now()),
                    contentUtil.shiftChangeUserFrom(), userAccepted.getId(), userInvolving.getId(), shift.getId(), NOTIFICATION);
            newsDB.createNotification(notification);

            //Creates update notification for user who accepted the shift change
            NewsFeedItem notification2 = new NewsFeedItem(-1, Timestamp.from(Instant.now()),
                    contentUtil.shiftChangeUserTo(),  userInvolving.getId(), userAccepted.getId(), shift.getId(), NOTIFICATION);
            newsDB.createNotification(notification2);

            newsDB.updateResolvedNotifications(userInvolving.getId(),shift.getId(), NewsFeedItem.NewsFeedCategory.SHIFT_CHANGE_EMPLOYEE);

        }
        //If the employees do not want the shift, check if there are any other users pending confirmation
        else{
            statusNewsfeed = newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
            if(newsDB.getShiftChangeCountPending(shift.getId(), userInvolving.getId())==0){
              int adminId = userDB.getAdminId();
                if(adminId == 0) {
                    System.out.println("Could not find admin user");
                    return false;
                }
                NewsFeedItem notification = new NewsFeedItem(-1, timestamp,
                        contentUtil.shiftChangeAdmin(userInvolving), adminId,userInvolving.getId(),
                        shift.getId(), NewsFeedItem.NewsFeedCategory.SHIFT_CHANGE_ADMIN);
                int status =  newsDB.createNotification(notification);

                if (status ==0){
                    return false;
                }
            }
        }
        //Remove current newsFeedItem
        return statusNewsfeed;
    }

    /** For administrator to manually approve shift change
     * @param newsFeedItem - the ID of the news feed notification
     * @param shiftAccepted - boolean if shift change is accepted or not
     * @return True if successful
     */
    private static boolean approveShiftChangeAdmin(NewsFeedItem newsFeedItem, boolean shiftAccepted){
        boolean status = true;
        User userInvolving = userDB.getUserById(newsFeedItem.getUserIdInvolving());
        Shift shift = shiftDB.getShift(newsFeedItem.getShiftId());

        if(shiftAccepted) {

                //Sets news feed items resolved
            if(!newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true)) return false;

            if(shift == null) return false;
            //Creates new update notification to the user who wants to change shift.
            NewsFeedItem notification = new NewsFeedItem(-1, Timestamp.from(Instant.now()), contentUtil.shiftChangeUserFrom(),
                    userInvolving.getId(), userInvolving.getId(), shift.getId(), NOTIFICATION);

            return newsDB.createNotification(notification) != 0;

        }
        else {
            //Creates notification for user
            NewsFeedItem notification = new NewsFeedItem(-1, Timestamp.from(Instant.now()), contentUtil.shiftChangeUserFromNotAccepted(),
                    userInvolving.getId(), userInvolving.getId(), shift.getId(), NOTIFICATION);

            //Removes admin notification if not accepted
            return newsDB.setNewsFeedItemResolved(newsFeedItem.getFeedId(), true);
        }

    }

    /** finds a responsible employee for a shift, prefers nurses, then health_workers, then assistants
     * @param shiftId - the shift that needs a new employee responsible
     * @return User obj for the new responsible employee
     */
    public static User findResponsibleUserForShift(int shiftId){
        ArrayList<User> users = shiftDB.getUsersFromShift(shiftId);
        LinkedList<User> sortedUsers = new LinkedList<>();

        for(User user : users){
            User bestUser = sortedUsers.peekFirst();
            if(bestUser == null){
                sortedUsers.addFirst(user);
            }
            else if (bestUser.getWorkPercentage() <= user.getWorkPercentage()) {
                if (Math.abs(bestUser.getWorkPercentage() - user.getWorkPercentage()) < 0.001) {
                    if (bestUser.getCategory() == User.UserCategory.ASSISTANT) {
                        sortedUsers.addFirst(user);
                    } else if (bestUser.getCategory() == User.UserCategory.HEALTH_WORKER && user.getCategory() != User.UserCategory.ASSISTANT) {
                        sortedUsers.addFirst(user);
                    } else {
                        sortedUsers.addLast(user);
                    }
                } else {
                    sortedUsers.addFirst(user);
                }
            }
            else {
                sortedUsers.addLast(user);
            }
        }
        return sortedUsers.peekFirst();
    }

}
