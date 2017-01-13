package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.ShiftUser;
import no.ntnu.stud.minvakt.data.ShiftUserBasic;
import no.ntnu.stud.minvakt.data.User;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by evend on 1/10/2017.
 */
public class ShiftDBManager extends DBManager {
    public ShiftDBManager(){
        super();
    }

    private final String sqlCreateNewShift = "INSERT INTO shift VALUES(DEFAULT,?,?,?,?);";
    private final String sqlCreateNewShiftStaff = "INSERT INTO employee_shift VALUES(?,?,?,?);";
    private final String sqlGetLastID = "SELECT LAST_INSERT_ID();";
    private final String sqlDeleteShift = "DELETE FROM shift WHERE shift_id=?;";
    private final String sqlDeleteShiftStaff = "DELETE FROM employee_shift WHERE shift_id=?;";
    private final String sqlGetShiftUser = "SELECT user_id, first_name, last_name, category, responsibility, valid_absence FROM employee_shift " +
            "NATURAL JOIN user NATURAL JOIN employee WHERE shift_id = ?;";
    private final String sqlGetShift = "SELECT shift_id, staff_number, date, time, dept_id FROM shift WHERE shift_id = ?;";
    private final String addEmployeeToShift = "INSERT INTO employee_shift VALUES(?,?,?,?);";
    private final String deleteEmployeeFromShift = "DELETE FROM employee_shift WHERE shift_id = ? and user_id = ?;";
    private final String getShiftWithUserId = "SELECT shift_id, date, time FROM shift WHERE shift_id IN (SELECT shift_id FROM employee_shift WHERE user_id = ?) AND date >= CURDATE()" +
            "ORDER BY date ASC, time ASC;";

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
                log.log(Level.WARNING, "Issue with creating new shift, data rolled back");
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
                prep = conn.prepareStatement(sqlDeleteShiftStaff);
                prep.setInt(1,shiftId);
                log.fine(prep.toString());
                status = prep.executeUpdate();
                if(status != 0){
                    prep = conn.prepareStatement(sqlDeleteShift);
                    prep.setInt(1, shiftId);
                    log.fine(prep.toString());
                    status = prep.executeUpdate();
                }
                else
                    throw new SQLException("Could not delete shift, rolling back");
            }
            catch (SQLException e){
                rollbackStatement();
                status = 0;
                log.log(Level.WARNING, "Issue with deleting shift with ID = " + shiftId, e);
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
                            res.getString("first_name") +" "+ res.getString("last_name"),
                            User.UserCategory.valueOf(res.getInt("category")),
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
            catch (SQLException e){
                rollbackStatement();
                log.log(Level.WARNING, "Not able to get shift from shift ID = " + shiftId, e);
            }
            finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return out;
    }
    /*
        Adds new employee to shift. Send a shiftUser object to the correct path
        (shift you would like to add to)
     */
    public boolean addEmployeeToShift(ShiftUser shiftUser, int shiftId){
        boolean out = false;
        if(setUp()){

            try {
                conn = getConnection();
                prep = conn.prepareStatement(addEmployeeToShift);
                prep.setInt(1,shiftUser.getUserId());
                prep.setInt(2, shiftId);
                prep.setBoolean(3, shiftUser.isResponsibility());
                prep.setBoolean(4,shiftUser.isValid_absence());
                out = prep.executeUpdate() != 0;

            }
            catch (SQLException e){
                log.log(Level.WARNING, "Not able to get shift from shift ID = " + shiftId, e);
            }
            finally {
                finallyStatement(prep);
            }
        }
        return out;
    }
    public boolean deleteEmployeeFromShift(int userId, int shiftId){
        boolean out = false;
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(deleteEmployeeFromShift);
                prep.setInt(1,shiftId);
                prep.setInt(2, userId);
                out = prep.executeUpdate() != 0;

            }
            catch (SQLException e){
                log.log(Level.WARNING, "Not able to delete shift with shift ID = " + shiftId + " and user ID = " + userId, e);
            }
            finally {
                finallyStatement(prep);
            }
        }
        return out;
    }

    public ArrayList<ShiftUserBasic> getShiftWithUserId(int userId){
        ArrayList<ShiftUserBasic> out = new ArrayList<>();
        if(setUp()){
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(getShiftWithUserId);
                prep.setInt(1, userId);
                res = prep.executeQuery();
                while(res.next()){
                    out.add(new ShiftUserBasic(
                            res.getInt("shift_id"),
                            res.getDate("date"),
                            Shift.ShiftType.valueOf(res.getInt("time"))
                            )
                    );
                }
            }
            catch (SQLException e){
                log.log(Level.WARNING, "Not able to get shift with userId = " + userId, e);
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }
}
