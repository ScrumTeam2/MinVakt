package no.ntnu.stud.minvakt.database;

import java.util.ArrayList;
import java.sql.*;

/**
 * Created by AnitaKristineAune on 12.01.2017.
 */
public class OvertimeDBManager extends DBManager{

    // user_id, date, start_time, end_time
    private final String sqlSetOvertime = "INSERT INTO overtime VALUES(?,?,?,?);";
    private final String sqlGetOvertime = "SELECT ";

    Connection conn;
    PreparedStatement prep;

    public OvertimeDBManager(){
        super();
    }

    // registers overtime on a given user. Returns true or false
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

    // returns overtime on given user
    public ArrayList<Integer> getOvertime(){

    }

}
