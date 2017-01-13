package no.ntnu.stud.minvakt.database;
import no.ntnu.stud.minvakt.data.Overtime;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.Arrays;
import java.util.logging.Level;

/**
 * Created by AnitaKristineAune on 12.01.2017.
 */
public class OvertimeDBManager extends DBManager{


    private final String sqlSetOvertime = "INSERT INTO overtime VALUES(?,?,?,?);";
    private final String sqlGetRowCount = "SELECT COUNT(*) FROM overtime WHERE user_id=? AND date BETWEEN ? AND ?;";
    private final String sqlGetOvertime = "SELECT date, start_time, end_time FROM overtime WHERE user_id = ? AND date BETWEEN ? AND ?;";

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

    // Returns array with Overtime object on given userId. It contains hours and date from given start date til end date
    public Overtime[] getOvertimeList(int userId, Date startDate, Date endDate){

        Overtime listObject = null;
        int rowCount = getRowCount(userId, startDate, endDate);

        Overtime[] timeList = new Overtime[rowCount];

        ResultSet res = null;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetOvertime);
                prep.setInt(1, userId);
                prep.setDate(2, startDate);
                prep.setDate(3, endDate);

                res = prep.executeQuery();

                int index = 0;
                while(res.next()){

                    listObject = new Overtime(res.getDate("date"), res.getInt("start_time"), res.getInt("end_time"));
                    timeList[index] = listObject;

                    index++;
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
    public int getOvertimeHours(int userId, Date startDate, Date endDate){
        int hours = 0;

        Overtime[] list = getOvertimeList(userId, startDate, endDate);
        for(int i = 0; i < list.length; i++){
            if(list[i].getEndTime() > list[i].getStartTime()) {
                hours += list[i].getEndTime() - list[i].getStartTime();
            } else {
                hours += (list[i].getEndTime() + 90) - list[i].getStartTime();
            }
        }
        return hours;
    }

    // Returns number of rows
    public int getRowCount(int userId, Date startDate, Date endDate){
        int count = 0;
        ResultSet res = null;
        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetRowCount);
                prep.setInt(1,userId);
                prep.setDate(2, startDate);
                prep.setDate(3, endDate);

                res = prep.executeQuery();
                while(res.next()){
                    count += res.getInt("COUNT(*)");
                }
            } catch (SQLException sqlE){
                log.log(Level.WARNING, "Error getting row count for user with ID = " + userId, sqlE);
            } finally{
                endTransaction();
                finallyStatement(prep);
            }
        }
        return count;
    }
}
