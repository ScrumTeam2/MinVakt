package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.ShiftUser;
import no.ntnu.stud.minvakt.data.User;

import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by evend on 1/10/2017.
 */
public class ShiftDBManager extends DBManager {
    public ShiftDBManager(){
        super();
    }

    private final String sqlCreateNewShift = "INSERT INTO shift VALUES(DEFAULT,?,?,?,?)";
    private final String sqlCreateNewShiftStaff = "INSERT INTO employee_shift VALUES(?,?,?,?)";
    private final String sqlGetLastID = "SELECT LAST_INSERT_ID()";
    private final String sqlDeleteShift = "DELETE FROM shift WHERE shift_id=?";
    private final String sqlDeleteShiftStaff = "DELETE FROM employee_shift WHERE shift_id=?";
    private final String sqlGetShiftUser = "SELECT user_id, responsibility, valid_absence FROM employee_shift WHERE shift_id = ?";
    private final String sqlGetShift = "SELECT shift_id, staff_number, date, time, dept_id FROM shift WHERE shift_id=?";

    Connection conn;
    PreparedStatement prep;

    /*
        Creates new shift for a specific employee

        Returns:
            If not successful - negative number (-1)
            if successful - shiftID

     */
    public int createNewShift(Shift shift) {
        int out = -1;
        if (setUp()) {
            try {
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlCreateNewShift);

                prep.setInt(1, shift.getStaffNumb());
                prep.setDate(2, shift.getDate());
                prep.setInt(3, shift.getType().getValue());
                prep.setInt(4, shift.getDeptId());
                System.out.println(prep.toString());
                if(prep.executeUpdate() != 0){
                    prep = conn.prepareStatement(sqlGetLastID);
                    ResultSet res = prep.executeQuery();
                    if(res.next()) {
                        //Last auto incremented value
                        shift.setId(res.getInt(1));
                        ArrayList<ShiftUser> shiftUsers = shift.getShiftUsers();
                        for(ShiftUser shiftUser : shiftUsers){
                            prep = conn.prepareStatement(sqlCreateNewShiftStaff);
                            prep.setInt(1, shiftUser.getUserId());
                            prep.setInt(2,shift.getId());
                            prep.setBoolean(3,shiftUser.isResponsibility());
                            prep.setBoolean(4,shiftUser.isValid_absence());
                            if(prep.executeUpdate() == 0){
                                throw new SQLException("User info not added, rolled back");
                            }
                        }
                        out = shift.getId();
                    }

                }
                else{
                    throw new SQLException("Database not updated, rolled back");
                }

            } catch (SQLException sqle) {
                rollbackStatement();
                System.err.println("Issue with creating new shift, data rolled back");
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
    public boolean deleteShift(int shiftId){
        int status = 0;
        if(setUp()){
            try {
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlDeleteShift);
                prep.setInt(1, shiftId);
                status = prep.executeUpdate();
                if(status != 0){
                    prep = conn.prepareStatement(sqlDeleteShiftStaff);
                    prep.setInt(1,shiftId);
                    status = prep.executeUpdate();
                }
                else
                    throw new SQLException("Could not delete shift, rolling back");
            }
            catch (SQLException sqle){
                rollbackStatement();
                status = 0;
                System.err.println("Issue with deleting shift with ID = "+shiftId);
                sqle.printStackTrace();
            }
            finally {
                endTransaction();
                closeConnection();
            }
        }
        return status != 0;
    }
    public Shift getShift(int shiftId){
        Shift out = null;
        if(setUp()){
            ResultSet res = null;
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetShiftUser);
                prep.setInt(1,shiftId);
                res = prep.executeQuery();
                ArrayList<ShiftUser> shiftUsers = new ArrayList<ShiftUser>();
                while(res.next()){

                    shiftUsers.add(new ShiftUser(res.getInt("user_id"),
                            res.getBoolean("responsibility"),
                            res.getBoolean("valid_absence")));
                }
                prep = conn.prepareStatement(sqlGetShift);
                prep.setInt(1, shiftId);
                res = prep.executeQuery();
                if(res.next())
                    out = new Shift(res.getInt("shift_id"),
                            res.getInt("staff_number"),
                            res.getDate("date"),
                            res.getInt("time"),
                            res.getInt("dept_id"),
                            shiftUsers);
            }
            catch (SQLException sqle){
                rollbackStatement();
                System.err.println("Not able to get shift from shift ID = "+shiftId);
                sqle.printStackTrace();
            }
            finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return out;
    }
}
