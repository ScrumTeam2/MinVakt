package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.*;

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
    private final String sqlCreateNewShiftStaff = "INSERT INTO employee_shift VALUES(?,?,?,?,?);";
    private final String sqlGetLastID = "SELECT LAST_INSERT_ID();";
    private final String sqlDeleteShift = "DELETE FROM shift WHERE shift_id=?;";
    private final String sqlDeleteShiftStaff = "DELETE FROM employee_shift WHERE shift_id=?;";
    private final String sqlGetShiftUser = "SELECT user_id, first_name, last_name, category, responsibility, valid_absence FROM employee_shift " +
            "NATURAL JOIN user NATURAL JOIN employee WHERE shift_id = ?;";
    private final String sqlGetShift = "SELECT shift_id, staff_number, date, time, dept_id FROM shift WHERE shift_id = ?;";
    private final String addEmployeeToShift = "INSERT INTO employee_shift VALUES(?,?,?,?,?);";
    private final String deleteEmployeeFromShift = "DELETE FROM employee_shift WHERE shift_id = ? and user_id = ?;";
    private final String getShiftWithUserId = "SELECT shift_id, date, time FROM shift WHERE shift_id IN (SELECT shift_id FROM employee_shift WHERE user_id = ?)" +
            " AND date >= ? ORDER BY date ASC, time ASC;";

    private final String sqlGetShiftHours = "SELECT COUNT(*) shift_id FROM employee_shift NATURAL JOIN shift WHERE user_id =? AND DATE BETWEEN ? AND ?";
    private final String sqlSetShiftChange = "UPDATE employee_shift SET shift_change=? WHERE shift_id =? AND user_id =?";
    private final String sqlGetShifts = "SELECT shift.shift_id, date, time, staff_number, COUNT(employee_shift.shift_id) as current_staff_numb " +
            "FROM shift JOIN employee_shift ON(shift.shift_id = employee_shift.shift_id) WHERE date >= ? " +
            "AND date <= DATE_ADD(?, INTERVAL ? DAY) AND valid_absence = 0 GROUP BY shift.shift_id ORDER BY date ASC, time ASC;";
    private final String sqlGetShiftsIsUser = "SELECT user_id FROM employee_shift WHERE user_id = ? AND shift_id = ?";
    private final String sqlSetStaffNumberOnShift = "UPDATE shift SET staff_number = ? WHERE shift_id = ?";

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
                java.sql.Date sqlDate = new java.sql.Date(shift.getDate().getTime());
                System.out.println("sqlDate: "+sqlDate);
                prep.setDate(2, sqlDate);
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
                            prep.setBoolean(5,false);

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
                log.log(Level.WARNING, "Issue with creating new shift, data rolled back", sqle);
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
                prep.setBoolean(4, shiftUser.isValid_absence());
                prep.setBoolean(5, false);
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

    public ArrayList<ShiftUserBasic> getShiftWithUserId(int userId, Date date){
        ArrayList<ShiftUserBasic> out = new ArrayList<>();
        if(setUp()){
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(getShiftWithUserId);
                prep.setInt(1, userId);
                prep.setDate(2,date);
                res = prep.executeQuery();
                while(res.next()){
                    out.add(new ShiftUserBasic(
                                    res.getInt("shift_id"),
                                    res.getDate("date"),
                                    Shift.ShiftType.valueOf(res.getInt("time"))
                    ));

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

    /*
     Calculates number of hours an employee has worked from a given start date to an end date.
     NB overtime hours are calculated in OvertimeDBManager
      */

    public int getShiftHours(int userId, Date startDate, Date endDate){
        int out = 0;
        ResultSet res = null;
        int shiftLength = 32; // Given each shift is 8 hours (4 * 8)

        if(setUp()){
            try {
               conn = getConnection();
               prep = conn.prepareStatement(sqlGetShiftHours);

               prep.setInt(1,userId);
               prep.setDate(2,startDate);
               prep.setDate(3, endDate);

               res = prep.executeQuery();
               while(res.next()){
                   out += res.getInt("shift_id");
               }
               out *= shiftLength;

            } catch (SQLException sqlE){
                log.log(Level.WARNING, "Error getting total number of hours for user with ID = " + userId);
            } finally{
                finallyStatement(prep);
            }
        }
        return out;
    }

    // Registers a shift as available for change. Returns true or false
    public boolean setShiftChange(int shiftId, int userId){
        int out = 0;
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement(sqlSetShiftChange);
                prep.setInt(1, 1);
                prep.setInt(2, shiftId);
                prep.setInt(3, userId);
                out = prep.executeUpdate();

            } catch(SQLException sqlE){
                log.log(Level.WARNING, "Error setting 'shift_change' = 1 for shift with ID = " + shiftId, sqlE);
            } finally{
                finallyStatement(prep);
            }
        }
        return out != 0;
    }

    private final String sqlGetCandidates = 
            "SELECT user.*, COUNT(*) shifts_worked FROM employee_shift " +
            "LEFT JOIN shift USING(shift_id) " +
            "NATURAL JOIN user " +
            "WHERE shift.date BETWEEN ? AND ? " +
            "AND user.category != 0 " +
            "GROUP BY user_id " +
            "UNION " +
            "SELECT *, 0 AS shifts_worked FROM user " +
            "WHERE category != 0 " +
            "ORDER BY shifts_worked DESC " +
            "LIMIT ?";

    public ArrayList<UserBasicWorkHours> getOrdinaryWorkHoursForPeriod(Date start, Date end, int limit) {
        ArrayList<UserBasicWorkHours> users = new ArrayList<>();
        ResultSet res;
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetCandidates);
                prep.setDate(1, start);
                prep.setDate(2, end);
                prep.setInt(3, limit);
                res = prep.executeQuery();
                while (res.next()){
                    int userId = res.getInt("user_id");
                    String firstName =res.getString("first_name");
                    String lastName = res.getString("last_name");
                    int category = res.getInt("category");
                    int normalShifts = res.getInt("shifts_worked");
                    UserBasicWorkHours user = new UserBasicWorkHours(userId,firstName,lastName, User.UserCategory.valueOf(category), normalShifts, 0);
                    users.add(user);
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "Could not get work hour list", e);
            }
        }
        return users;
    }
    public ArrayList<ShiftUserAvailability> getShifts(int daysForward, int userId, Date date){
        ArrayList<ShiftUserAvailability> out = new ArrayList<>();
        if (setUp()){
            ResultSet res = null;
            ResultSet res2 = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetShifts);
                prep.setDate(1, date);
                prep.setDate(2, date);
                prep.setInt(3, daysForward);
                startTransaction();
                res = prep.executeQuery();
                boolean isInShift = false;
                while(res.next()){
                    isInShift = false;
                    int shiftId = res.getInt("shift_id");
                    prep = conn.prepareStatement(sqlGetShiftsIsUser);
                    prep.setInt(1,userId);
                    prep.setInt(2, shiftId);
                    res2 = prep.executeQuery();
                    if(res2.next()){
                        isInShift = true;
                    }
                    boolean isAvailable = res.getInt("staff_number") != res.getInt("current_staff_numb");

                    ShiftUserAvailability obj = new ShiftUserAvailability(
                            shiftId, res.getDate("date"),
                            Shift.ShiftType.valueOf(res.getInt("time")), isAvailable,
                            isInShift
                    );
                    out.add(obj);

                }

            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Error getting shifts with connected to availability and user");
                sqle.printStackTrace();
            }
            finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return out;
    }
    public boolean setStaffNumberOnShift(int shiftId, int staffNumber){
        int status = 0;
        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement(sqlSetStaffNumberOnShift);
                prep.setInt(2, shiftId);
                prep.setInt(1,staffNumber);
                status = prep.executeUpdate();
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Error editing number of staff number on shift "+shiftId);
            }
            finally {
                finallyStatement(prep);
            }
        }
        return status != 0;
    }
}
