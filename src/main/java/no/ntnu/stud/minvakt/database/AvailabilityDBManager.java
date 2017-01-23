package no.ntnu.stud.minvakt.database;

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
    private final String sqlGetAvailableShiftsForDate = "SELECT *, COUNT(employee_shift.shift_id) AS current_staff_numb FROM shift JOIN employee_shift ON (employee_shift.shift_id = shift.shift_id) JOIN department ON (shift.dept_id = department.dept_id) WHERE valid_absence=0 AND shift_change = 0 AND date =? AND approved = TRUE GROUP BY date, time, shift.dept_id";
    private final String sqlGetAvailabilityUserBasic = "SELECT user_id, first_name, last_name, category FROM availability NATURAL JOIN user WHERE shift_id=?";

    Connection conn;
    PreparedStatement prep;

    public AvailabilityDBManager(){
        super();
    }


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
                System.out.println(prep);
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

    // Find all available shifts for a specific date
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
                                res.getString("dept_name")
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
}