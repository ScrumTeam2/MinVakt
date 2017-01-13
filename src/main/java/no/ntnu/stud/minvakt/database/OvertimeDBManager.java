package no.ntnu.stud.minvakt.database;

import java.sql.*;
import java.util.logging.Level;

/**
 * Created by AnitaKristineAune on 12.01.2017.
 */
public class OvertimeDBManager extends DBManager{

    private final String sqlSetOvertime = "INSERT INTO overtime VALUES(?,?,?,?);";
    private final String sqlGetRowCount = "SELECT COUNT(*) FROM overtime WHERE user_id=?";
    private final String sqlGetOvertime = "SELECT date, start_time, end_time FROM overtime WHERE user_id = ? AND date LIKE ? ORDER BY date ASC LIMIT ?;";

    // private final String sqlGetOvertimeHours = "SELECT start_time, end_time FROM overtime WHERE user_id=?";
    // private final String sqlGetOvertime = "SELECT start_time, end_time FROM overtime WHERE user_id=?";

    Connection conn;
    PreparedStatement prep;

    public OvertimeDBManager(){
        super();
    }

    // Registers overtime on a given user. Returns true or false
    public boolean setOvertime(int userId, Date date, int startTime, int endTime){
        int out = 0;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlSetOvertime);

                prep.setInt(1, userId);
                prep.setDate(2, date);
                prep.setInt(3, startTime);
                prep.setInt(4, endTime);

                out = prep.executeUpdate();

            } catch (SQLException sqlE){
                System.err.println("Error register overtime on user with ID = " + userId + " on date: " + date);
                sqlE.printStackTrace();
            } finally {
                endTransaction();
                finallyStatement(prep);
            }
        }

        return out != 0;
    }

    public int getRowCount(int userId){
        int count = 0;
        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetRowCount);
                prep.setInt(1,userId);
                count = prep.executeUpdate();
            } catch (SQLException sqlE){
                log.log(Level.WARNING, "Error getting row count for user with ID = " + userId, sqlE);
            } finally{
                endTransaction();
                finallyStatement(prep);
            }
        }
        return count;
    }

    // Returns array with overtime (date, start_time, end_time) on given userID. Hours and date from given start date until given number of days
    public int[][] getOvertimeList(int userId, Date date, int days){
        int rowCount = getRowCount(userId);

        int[][] timeList = new int[rowCount][3];

        ResultSet res = null;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetOvertime);
                prep.setInt(1, userId);
                prep.setDate(2, date);
                prep.setInt(3, days);

                res = prep.executeQuery();

                while(res.next()){
                    for(int i = 0; i < timeList.length; i++){
                        timeList[i][0] = res.getInt("date");
                        timeList[i][1] = res.getInt("start_time");
                        timeList[i][2] = res.getInt("end_time");
                    }
                }

            } catch (SQLException sqlE){
                log.log(Level.WARNING, "Error returning overtime for user with ID = " + userId, sqlE);
            } finally {
                endTransaction();
                finallyStatement(prep);
            }
        }
        return timeList;
    }

    // Returns overtime for given user. Hours are returned from given start date until given number of days
    public int getOvertimeHours(int userId, Date date, int days){
        int hours = 0;

        int[][] list = getOvertimeList(userId, date, days);
        for(int i = 0; i < list.length; i++){
            if(list[i][3] > list[i][2]) {
                hours += list[i][3] - list[i][2];
            } else {
                hours += (list[i][3] + 90) - list[i][2];
            }
        }
        return hours;
    }
}