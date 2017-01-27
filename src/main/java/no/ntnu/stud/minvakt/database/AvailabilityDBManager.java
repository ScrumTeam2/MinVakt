package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.*;
import no.ntnu.stud.minvakt.data.shift.ShiftUserAvailability;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasicWorkHours;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shift.ShiftAvailable;

import java.util.ArrayList;
import java.sql.*;
import java.util.logging.Level;

public class AvailabilityDBManager extends DBManager{

    private final String sqlGetAvailability = "SELECT user_id, first_name, last_name FROM availability NATURAL JOIN user WHERE shift_id=?";
    private final String sqlSetAvailability = "INSERT INTO availability VALUES(?,?);";
    private final String sqlDeleteAvailability = "DELETE FROM availability WHERE user_id=? AND shift_id=?";
    private final String sqlGetAvailabilityForUser = "SELECT * FROM availability WHERE user_id = ?";
    private final String sqlGetAvailableShiftsForDate = "SELECT *, COUNT(employee_shift.shift_id) AS current_staff_numb FROM shift JOIN employee_shift ON (employee_shift.shift_id = shift.shift_id) JOIN department ON (shift.dept_id = department.dept_id) WHERE valid_absence=0 AND shift_change = 0 AND date =? AND approved = TRUE GROUP BY date, time, shift.dept_id";
    private final String sqlGetAvailabilityUserBasic = "SELECT user_id, first_name, last_name, category FROM availability NATURAL JOIN user WHERE shift_id=?";
    private final String sqlGetShiftAvailableForDate = "SELECT shift.shift_id, date, time, staff_number, COUNT(employee_shift.shift_id) as current_staff_numb, " +
            "dept_name FROM shift JOIN employee_shift ON(shift.shift_id = employee_shift.shift_id) " +
            " JOIN department ON (shift.dept_id = department.dept_id) WHERE date >= ? " +
            "AND date <= DATE_ADD(?, INTERVAL ? DAY) AND valid_absence = 0 AND removed = 0 GROUP BY shift.shift_id,shift.dept_id ORDER BY date ASC, time ASC;";
    private final String sqlGetShiftsIsUser = "SELECT user_id FROM employee_shift WHERE user_id = ? AND shift_id = ? AND removed = 0";

    Connection conn;
    PreparedStatement prep;

    public AvailabilityDBManager(){
        super();
    }


    /*TODO delete if not used
    // Find available staff for a given shift, returns arraylist with userIDs
    public ArrayList<Integer> getAvailability(int shiftID){
        ArrayList<Integer> userList = new ArrayList<>();

        ResultSet res = null;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetAvailability);

                prep.setInt(1, shiftID);
                res = prep.executeQuery();

                while(res.next()){
                    userList.add(res.getInt("user_id"));
                }

            } catch (SQLException sqlE) {
                log.log(Level.WARNING, "Error finding available staff for shift with ID = " + shiftID, sqlE);
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return userList;
    }
    */

    /* TODO: delete if not used
    // Find available staff for a given shift, returns arraylist with userIDs
    public ArrayList<Integer> getAvailabilityForUser2(int userId){
        ArrayList<Integer> userList = new ArrayList<>();
        ResultSet res = null;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetAvailability);

                prep.setInt(1, userId);
                res = prep.executeQuery();

                while(res.next()){
                    userList.add(res.getInt("user_id"));
                }

            } catch (SQLException sqlE) {
                log.log(Level.WARNING, "Error finding available staff for shift with ID = " + userId, sqlE);
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return userList;
    }
    */

    public UserAvailableShifts getAvailabilityForUser(int userId){
        ArrayList<Integer> shiftList = new ArrayList<>();
        UserAvailableShifts u = null;
        ResultSet res = null;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetAvailabilityForUser);

                prep.setInt(1, userId);
                res = prep.executeQuery();

                while(res.next()){
                    shiftList.add(res.getInt("shift_id"));
                }
                u = new UserAvailableShifts(userId, shiftList);
            } catch (SQLException sqlE) {
                log.log(Level.WARNING, "Error grabbing shifts for user with user ID = " + userId, sqlE);
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return u;
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
                log.log(Level.WARNING, "Error registering user with ID = " + userID + " available for shift with ID = " + shiftID, sqlE);
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

                prep = conn.prepareStatement(sqlDeleteAvailability);
                prep.setInt(1, userID);
                prep.setInt(2, shiftID);
                out = prep.executeUpdate();

            } catch (SQLException sqlE) {
                log.log(Level.WARNING, "Error: Deleting availability on user with ID = " + userID + " and shift with ID = " + shiftID, sqlE);
            } finally {
                endTransaction();
                finallyStatement(prep);
            }
        }
        return out != 0;
    }

    // Find available staff for a given shift, returns arraylist with UserbasicWorkHours objects
    public ArrayList<UserBasicWorkHours> getAvailabilityUserBasic(int shiftID){
        ArrayList<UserBasicWorkHours> userList = new ArrayList<>();

        ResultSet res = null;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetAvailabilityUserBasic);

                prep.setInt(1, shiftID);
                res = prep.executeQuery();

                while(res.next()){
                    UserBasicWorkHours tempUser = new UserBasicWorkHours();
                    tempUser.setId(res.getInt("user_id"));
                    tempUser.setFirstName(res.getString("first_name"));
                    tempUser.setLastName(res.getString("last_name"));
                    tempUser.setCategory(User.UserCategory.valueOf(res.getInt("category")));
                    userList.add(tempUser);
                }

            } catch (SQLException sqlE) {
                log.log(Level.WARNING, "Error finding available staff for shift with ID = " + shiftID, sqlE);
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return userList;
    }

    public ArrayList<ShiftAvailable> getShiftsForDate(int daysForward, int userId, Date date){
        ArrayList<ShiftAvailable> out = new ArrayList<>();
        if (setUp()){
            ResultSet res = null;
            ResultSet res2 = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetShiftAvailableForDate);
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

                    ShiftAvailable obj = new ShiftAvailable(
                            shiftId, res.getDate("date"),
                            Shift.ShiftType.valueOf(res.getInt("time")),
                            res.getString("dept_name"),
                            isAvailable,
                            isInShift
                    );
                    out.add(obj);

                }

            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Error getting shifts with connected to availability and user", sqle);
                sqle.printStackTrace();
            }
            finally {
                endTransaction();
                finallyStatement(res, prep);
                finallyStatement(res2, prep);
            }
        }
        return out;
    }

    /*TODO delete if not used
    // Find all available shifts for a specific date
    @Deprecated
    public ArrayList<ShiftAvailable> getAvailabilityForDate(String day){
        ArrayList<ShiftAvailable> out = new ArrayList<>();
        ResultSet res = null;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetAvailableShiftsForDate);

                prep.setString(1,day);
                res = prep.executeQuery();
                while(res.next()){
                    out.add(new ShiftAvailable(
                                res.getInt("shift_id"),
                                res.getDate("date"),
                                Shift.ShiftType.valueOf(res.getInt("time")),
                                res.getString("dept_name"),false,false
                            )
                    );
                }

            } catch (SQLException sqlE) {
                log.log(Level.WARNING, "Error finding available staff for shift with ID = " + day, sqlE);
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return out;
    }
    */

}