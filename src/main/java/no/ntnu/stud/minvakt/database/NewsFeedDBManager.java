package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.data.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by evend on 1/20/2017.
 */
public class NewsFeedDBManager extends DBManager{

    private final String sqlCreateNotification = "INSERT INTO newsfeed VALUES(DEFAULT,?,?,0,?,?,?)";
    private final String sqlDeleteNotification = "DELETE FROM newsfeed WHERE feed_id = ?";
    private final String sqlGetLastID = "SELECT LAST_INSERT_ID();";
    private final String sqlGetNewsFeedForUser = "SELECT * FROM newsfeed WHERE user_id = ?";
    private final String sqlGetNewsFeedForAdmin = "SELECT date_time, content, shift_id, FROM newsfeed NATURAL JOIN user " +
            "WHERE category = ?;";
    private final String sqlGetNewsFeedItem = "SELECT * FROM newsfeed WHERE feed_id = ?;";

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
                prep = conn.prepareStatement(sqlCreateNotification);

                prep.setTimestamp(1, notification.getDateTime());
                prep.setString(2, notification.getContent());
                prep.setInt(3, notification.getUserIdTo());
                prep.setInt(4, notification.getShiftId());
                prep.setInt(5, notification.getUserIdInvolving());
                id = prep.executeUpdate();
                if(id != 0){
                    prep = conn.prepareStatement(sqlGetLastID);
                    res = prep.executeQuery();
                    if(res.next()){
                        id = res.getInt(1);
                    }
                }

            } catch (SQLException sqle) {
                log.log(Level.WARNING, "Not able to add notification to news feed");
                sqle.printStackTrace();
            } finally {
                finallyStatement(prep);
            }
        }
        return id;
    }
    public boolean deleteNotification(int notificationId){
        int status = 0;
        if(setUp()) {
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlDeleteNotification);
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
                prep = conn.prepareStatement(sqlGetNewsFeedForUser);
                prep.setInt(1, userId);
                res = prep.executeQuery();
                while(res.next()){
                    out.add(
                            new NewsFeedItem(res.getInt("feed_id"),
                                    res.getTimestamp("date_time"),
                                    res.getString("content"),
                                    res.getInt("user_id"),
                                    res.getInt("shift_user_id"),
                                    res.getInt("shift_id"))
                    );
                }

            } catch (SQLException sqle) {
                log.log(Level.WARNING, "Not able to delete notification from news feed");
                sqle.printStackTrace();
            } finally {
                finallyStatement(prep);
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
                prep = conn.prepareStatement(sqlGetNewsFeedItem);
                prep.setInt(1, feedId);
                res = prep.executeQuery();
                if(res.next()){
                    notification = new NewsFeedItem(res.getInt("feed_id"),
                                    res.getTimestamp("date_time"),
                                    res.getString("content"),
                                    res.getInt("user_id"),
                                    res.getInt("shift_user_id"),
                                    res.getInt("shift_id")
                    );
                }

            } catch (SQLException sqle) {
                log.log(Level.WARNING, "Not able to get notification from news feed");
                sqle.printStackTrace();
            } finally {
                finallyStatement(prep);
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
                prep = conn.prepareStatement(sqlGetNewsFeedForAdmin);
                prep.setInt(1, User.UserCategory.ADMIN.getValue());
                res = prep.executeQuery();
                while(res.next()){
                    out.add(
                            new NewsFeedItem(res.getInt("feed_id"),
                                    res.getTimestamp("date_time"),
                                    res.getString("content"),
                                    res.getInt("user_id"),
                                    res.getInt("shift_user_id"),
                                    res.getInt("shift_id"))
                    );
                }

            } catch (SQLException sqle) {
                log.log(Level.WARNING, "Not able to delete notification from news feed");
                sqle.printStackTrace();
            } finally {
                finallyStatement(prep);
            }
        }
        return out;
    }
}

