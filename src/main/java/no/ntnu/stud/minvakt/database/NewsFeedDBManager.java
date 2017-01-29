package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.data.user.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by evend on 1/20/2017.
 */
public class NewsFeedDBManager extends DBManager{

    private final String sqlCreateNotification = "INSERT INTO newsfeed VALUES(DEFAULT,?,?,0,?,?,?,?,?)";
    private final String sqlDeleteNotification = "DELETE FROM newsfeed WHERE feed_id = ?";
    private final String sqlDeleteNotificationsForShift = "DELETE FROM newsfeed WHERE shift_id = ?";
    private final String sqlGetLastID = "SELECT LAST_INSERT_ID();";
    private final String sqlGetNewsFeedForUser = "SELECT * FROM newsfeed WHERE user_id = ? AND resolved = 0";
    private final String sqlGetNewsFeedForAdmin = "SELECT feed_id, date_time,content,newsfeed.category, start_time, user.user_id, shift_id, shift_user_id " +
            "FROM newsfeed JOIN user ON(user.user_id = newsfeed.user_id) WHERE user.category = ? AND resolved = 0;";
    private final String sqlGetNewsFeedItem = "SELECT * FROM newsfeed WHERE feed_id = ?;";
    private final String sqlSetNewsFeedItemResolved = "UPDATE newsfeed SET resolved = ? WHERE feed_id = ?;";

    private final String sqlGetNewsFeedIdFromOvertime = "SELECT feed_id FROM overtime JOIN newsfeed ON overtime.shift_id = newsfeed.shift_id AND overtime.user_id = newsfeed.shift_user_id AND overtime.start_time = newsfeed.start_time WHERE overtime.user_id = ? AND overtime.shift_id = ? AND overtime.start_time = ?;";
    private final String sqlGetShiftChangePendingCount = "SELECT COUNT(*) as pending FROM newsfeed WHERE category = 0 AND shift_id = ? AND shift_user_id = ? AND resolved = 0;";

    Connection conn;
    PreparedStatement prep;

    public NewsFeedDBManager(){
        super();
    }

    public int createNotification(NewsFeedItem notification){
        int id = 0;
        if(setUp()) {
            ResultSet res = null;
            try {
                conn = getConnection();
                PreparedStatement prep = conn.prepareStatement(sqlCreateNotification);

                prep.setTimestamp(1, notification.getDateTime());
                prep.setString(2, notification.getContent());
                prep.setInt(3,notification.getCategory().getValue());
                prep.setInt(4, notification.getUserIdTo());
                prep.setInt(5, notification.getShiftId());
                prep.setInt(6, notification.getUserIdInvolving());
                prep.setInt(7,notification.getStartTimeTimebank());
                id = prep.executeUpdate();
                if(id != 0){
                    prep = conn.prepareStatement(sqlGetLastID);
                    res = prep.executeQuery();
                    if(res.next()){
                        id = res.getInt(1);
                    }
                }

            } catch (SQLException e) {
                log.log(Level.WARNING, "Not able to add notification to news feed", e);
            } finally {
                finallyStatement(res, prep);
            }
        }
        return id;
    }
    public boolean deleteNotificationsForShift(int shiftId){
        int status = 0;
        if(setUp()) {
            try {
                conn = getConnection();
                PreparedStatement prep = conn.prepareStatement(sqlDeleteNotificationsForShift);
                prep.setInt(1,shiftId);
                status = prep.executeUpdate();

            } catch (SQLException e) {
                log.log(Level.WARNING, "Not able to delete notifications for shift " + shiftId + " from news feed", e);
            } finally {
                finallyStatement(prep);
            }
        }
        return status != 0;
    }
    public boolean deleteNotification(int notificationId){
        int status = 0;
        if(setUp()) {
            try {
                conn = getConnection();
                PreparedStatement prep = conn.prepareStatement(sqlDeleteNotification);
                prep.setInt(1,notificationId);
                status = prep.executeUpdate();

            } catch (SQLException sqle) {
                log.log(Level.WARNING, "Not able to delete notification from news feed");
                sqle.printStackTrace();
            } finally {
                finallyStatement(prep);
            }
        }
        return status != 0;
    }
    public ArrayList<NewsFeedItem> getNewsFeed(int userId){
        ArrayList<NewsFeedItem> out = new ArrayList<>();
        if(setUp()) {
            ResultSet res = null;
            try {
                conn = getConnection();
                PreparedStatement prep = conn.prepareStatement(sqlGetNewsFeedForUser);
                prep.setInt(1, userId);
                res = prep.executeQuery();
                while(res.next()){
                    out.add(
                            new NewsFeedItem(res.getInt("feed_id"),
                                    res.getTimestamp("date_time"),
                                    res.getString("content"),
                                    res.getInt("user_id"),
                                    res.getInt("shift_user_id"),
                                    res.getInt("shift_id"),
                            NewsFeedItem.NewsFeedCategory.valueOf(res.getInt("category")))
                    );
                }

            } catch (SQLException e) {
                log.log(Level.WARNING, "Not able to delete notification from news feed", e);
            } finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }
    public NewsFeedItem getNewsFeedItem(int feedId){
        NewsFeedItem notification = null;
        if(setUp()) {
            ResultSet res = null;
            try {
                conn = getConnection();
                PreparedStatement prep = conn.prepareStatement(sqlGetNewsFeedItem);
                prep.setInt(1, feedId);
                res = prep.executeQuery();
                if(res.next()){
                    notification = new NewsFeedItem(res.getInt("feed_id"),
                                    res.getTimestamp("date_time"),
                                    res.getString("content"),
                                    res.getInt("user_id"),
                                    res.getInt("shift_user_id"),
                                    res.getInt("shift_id"),
                                    NewsFeedItem.NewsFeedCategory.valueOf(res.getInt("category")),
                                    res.getInt("start_time")
                    );
                }

            } catch (SQLException e) {
                log.log(Level.WARNING, "Not able to get notification from news feed", e);
            } finally {
                finallyStatement(res, prep);
            }
        }
        return notification;
    }

    public ArrayList<NewsFeedItem> getNewsFeedAdmin(){
        ArrayList<NewsFeedItem> out = new ArrayList<>();
        if(setUp()) {
            ResultSet res = null;
            try {
                conn = getConnection();
                PreparedStatement prep = conn.prepareStatement(sqlGetNewsFeedForAdmin);
                prep.setInt(1, User.UserCategory.ADMIN.getValue());
                System.out.println(prep);

                res = prep.executeQuery();
                while(res.next()){
                    out.add(
                            new NewsFeedItem(res.getInt("feed_id"),
                                    res.getTimestamp("date_time"),
                                    res.getString("content"),
                                    res.getInt("user_id"),
                                    res.getInt("shift_user_id"),
                                    res.getInt("shift_id"),
                            NewsFeedItem.NewsFeedCategory.valueOf(res.getInt("category")),
                                    res.getInt("start_time")
                            )
                    );
                }

            } catch (SQLException e) {
                log.log(Level.WARNING, "Not able to delete notification from news feed", e);
            } finally {
                finallyStatement(res,prep);
            }
        }
        return out;
    }
    public boolean setNewsFeedItemResolved(int feedId, boolean resolved){
        int status = 0;
        if(setUp()) {
            try {
                conn = getConnection();
                PreparedStatement prep = conn.prepareStatement(sqlSetNewsFeedItemResolved);
                prep.setBoolean(1,resolved);
                prep.setInt(2,feedId);
                status = prep.executeUpdate();

            } catch (SQLException e) {
                log.log(Level.WARNING, "Not able to update notification resolve to "+resolved, e);
            } finally {
                finallyStatement(prep);
            }
        }
        return status != 0;
    }

    /**
     * @param userId - the ID of the employee who has worked overtime
     * @param shiftId - the ID of the shift
     * @param startTime - the time of day for given overtime
     * @return int - the ID of the news feed item
     */
    public int getNewsFeedIdThroughOvertime(int userId, int shiftId, int startTime){
        int feedId = 0;
        ResultSet res = null;

        if(setUp()){
            try{
                conn = getConnection();
                PreparedStatement prep = conn.prepareStatement(sqlGetNewsFeedIdFromOvertime);
                prep.setInt(1, userId);
                prep.setInt(2, shiftId);
                prep.setInt(3, startTime);
                res = prep.executeQuery();
                res.next();
                feedId = res.getInt("feed_id");
            } catch (SQLException e) {
                log.log(Level.WARNING, "Not able to find feedId for userID: "+userId+", shiftID: " +shiftId+ ", startTime: "+startTime, e);
            } finally {
                finallyStatement(res, prep);
            }
        }
        return feedId;
    }

    /**
     * @param shiftId the ID of the shift
     * @param shiftUserId the ID of the employee who wants to change their shift
     * @return int - count of the employees who have yet to accept or reject an offer to take a shift
     */
    public int getShiftChangeCountPending(int shiftId, int shiftUserId){
        int resolvedCount = 0;
        ResultSet res=null;
        //category = ? AND shift_id = ? AND shift_user_id = ? AND resolved = 0;";

        if(setUp()){
            try{
                conn = getConnection();
                PreparedStatement prep = conn.prepareStatement(sqlGetShiftChangePendingCount);
                prep.setInt(1, shiftId);
                prep.setInt(2, shiftUserId);
                res = prep.executeQuery();
                res.next();
                resolvedCount = res.getInt("pending");
            } catch (SQLException e) {
                log.log(Level.WARNING, "Not able to find unresolved user count for shiftID: " +shiftId+ " and shift_user: "+shiftUserId, e);
            } finally {
                finallyStatement(res, prep);
            }
        }
        return resolvedCount;
    }

    private static final String sqlUserHasFeed = "SELECT 1 FROM newsfeed WHERE user_id = ? AND feed_id = ?";

    /**
     * Checks if an user has access to a specified feed item
     * @param userId The ID of the user
     * @param feedId The ID of the feed item
     * @return True if the user has access
     */
    public boolean userHasFeed(int userId, int feedId) {
        if(!setUp()) {
            return false;
        }

        try {
            conn = getConnection();
            PreparedStatement prep = conn.prepareStatement(sqlUserHasFeed);
            prep.setInt(1, userId);
            prep.setInt(2, feedId);
            return prep.executeQuery().next();
        } catch (SQLException e) {
            log.log(Level.WARNING, "Unable to check if user " + userId + " has feed " + feedId, e);
        }
        return false;
    }
}

