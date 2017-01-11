package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.Shift;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by evend on 1/10/2017.
 */
public class ShiftDBManager extends DBManager {
    public ShiftDBManager(){
        super();
    }

    private final String sqlCreateNewShift = "INSERT INTO shift VALUES(DEFAULT,?,?,?,?,?,?)";
    private final String sqlGetLastID = "SELECT LAST_INSERT_ID()";
    private final String sqlDeleteShift = "DELETE FROM shift WHERE shift_id=?";

    Connection conn;
    PreparedStatement prep;

    /*
        Creates new shift for a specific employee

        Returns:
            If not successful - negative number (-1)
            if successful - shiftID

        Arguments:
            date - SQL date object with the date of shift start
            startTime - Integer representing how many 15 minute rotations from 00:00 the shift starts
            endTime - Integer representing how many 15 minute rotations from 00:00 the shift ends
            responsibility - Boolean telling whether the person is the shift supervisor
            userID - Integer with user primary key
     */
    public int createNewShift(Shift shift) {
        int out = -1;
        if (setUp()) {
            try {
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlCreateNewShift);

                prep.setBoolean(1, shift.isResponsible());
                prep.setBoolean(2, shift.isValidAbsence());
                prep.setDate(3, shift.getDate());
                prep.setInt(4, shift.getType().getValue());
                prep.setInt(5, shift.getUserId());
                prep.setInt(6, shift.getDeptId());
                System.out.println(prep.toString());
                if(prep.executeUpdate() != 0){
                    prep = conn.prepareStatement(sqlGetLastID);
                    ResultSet res = prep.executeQuery();
                    if(res.next()) {
                        //Last auto incremented value
                        out = res.getInt(1);
                    }
                }

            } catch (SQLException sqle) {
                System.err.println("Issue with creating new shift");
                sqle.printStackTrace();
            }
            finally {
                endTransaction();
                finallyStatement(prep);
            }
        }
        return out;
    }

    //Deletes a shift using the shift ID. Meant mainly for cleaning up database when running tests.
    public boolean deleteShift(int shiftID){
        int out = 0;
        if(setUp()){
            try {
                prep = getConnection().prepareStatement(sqlDeleteShift);
                prep.setInt(1,shiftID);
                out = prep.executeUpdate();
            }
            catch (SQLException sqle){
                System.err.println("Issue with deleting shift with ID = "+shiftID);
                sqle.printStackTrace();
            }
        }
        return out != 0;

    }
}
