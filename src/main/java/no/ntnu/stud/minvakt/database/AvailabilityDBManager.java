package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.data.Shift;
import java.util.ArrayList;

import java.sql.*;
import no.ntnu.stud.minvakt.data.ShiftUser;

/**
 * Created by AnitaKristineAune on 11.01.2017.
 */
public class AvailabilityDBManager extends DBManager{

    private final String sqlGetAvailability = "SELECT user_id, first_name, last_name FROM availability NATURAL JOIN user WHERE shift_id=?";
    private final String sqlSetAvailability = "INSERT INTO availability VALUES(?,?);";
    private final String sqlDeleteAvailability = "DELETE user_id, shift_id FROM availability WHERE user_id=? AND shift_id=?";

    Connection conn;
    PreparedStatement prep;

    public AvailabilityDBManager(){
        super();
    }


    // Find available staff for a given shift, returns arraylist with userIDs
    public ArrayList<ShiftUser> getAvailability(int shiftID){
        ArrayList<ShiftUser> userList = new ArrayList<>();

        ResultSet res = null;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetAvailability);

                prep.setInt(1, shiftID);
                res = prep.executeQuery();

                while(res.next()){
                    userList.add(new ShiftUser(
                            res.getInt("user_id"), res.getBoolean("responsibility"), res.getBoolean("valid_absence")));
                }


            } catch (SQLException sqlE){
                System.err.println("Error finding available staff for shift with ID = " +shiftID);
                sqlE.printStackTrace();
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return userList;
    }

    // Sets staff member available for given shift, returns true or false
    public boolean setAvailability(int userID, int shiftID){
        int out = 0;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlSetAvailability);

                prep.setInt(1, userID);
                prep.setInt(2, shiftID);
                out = prep.executeUpdate();
            } catch (SQLException sqlE){
                System.err.println("Error registering user with ID = " + userID + "available for shift with ID = " +shiftID);
                sqlE.printStackTrace();
            } finally {
                endTransaction();
                finallyStatement(prep);
            }
        }
        return out != 0;
    }

    // Disables the availability on given user for given shift
    public boolean deleteAvailability(int userID, int shiftID){
        int out = 0;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                // shiftID
                prep = conn.prepareStatement(sqlDeleteAvailability);
                prep.setInt(1, userID);
                prep.setInt(2, shiftID);

            } catch (SQLException sqlE) {
                System.err.println("Error: Deleting availability on user with ID = " + userID + " and shift with ID = " + shiftID);
            } finally {
                endTransaction();
                closeConnection();
            }
        }
        return out != 0;
    }
}