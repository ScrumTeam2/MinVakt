package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.shift.*;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasicWorkHours;
import no.ntnu.stud.minvakt.util.ShiftChangeUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by evend on 1/10/2017.
 */

public class ShiftDBManager extends DBManager {
    public ShiftDBManager(){
        super();
    }

    private final String sqlCreateNewShift = "INSERT INTO shift VALUES(DEFAULT,?,?,?,?,?);";
    private final String sqlCreateNewShiftStaff = "INSERT INTO employee_shift VALUES(?,?,?,?,?,DEFAULT);";
    private final String sqlGetLastID = "SELECT LAST_INSERT_ID();";
    private final String sqlDeleteShift = "DELETE FROM shift WHERE shift_id=?;";
    private final String sqlDeleteShiftStaff = "DELETE FROM employee_shift WHERE shift_id=?;";
    private final String sqlGetShiftUser = "SELECT user_id, first_name, last_name, category, responsibility, valid_absence FROM employee_shift " +
    "NATURAL JOIN user WHERE shift_id = ? AND removed = 0;";
    private final String sqlGetShift = "SELECT * FROM shift WHERE shift_id = ?;";

    private final String addEmployeeToShift = "INSERT INTO employee_shift VALUES(?,?,?,?,?,DEFAULT);";
    private final String deleteEmployeeFromShift = "UPDATE employee_shift SET removed = 1 WHERE shift_id = ? and user_id = ?;";
    private final String getShiftWithUserId = "SELECT shift_id, date, time FROM shift WHERE shift_id IN (SELECT shift_id FROM employee_shift WHERE user_id = ? AND removed = 0)" +
            " AND date >= ? AND approved = TRUE ORDER BY date ASC, time ASC;";


    private final String sqlGetNumberOfShifts = "SELECT COUNT(*) shift_id FROM employee_shift NATURAL JOIN shift WHERE user_id =? AND DATE BETWEEN ? AND ? AND shift.approved = TRUE ";
    private final String sqlSetShiftChange = "UPDATE employee_shift SET shift_change=? WHERE shift_id =? AND user_id =?";
    private final String sqlGetShifts = "SELECT shift.shift_id, date, time, staff_number, COUNT(employee_shift.shift_id) as current_staff_numb " +
            "FROM shift JOIN employee_shift ON(shift.shift_id = employee_shift.shift_id) WHERE date >= ? " +
            "AND date <= DATE_ADD(?, INTERVAL ? DAY) AND valid_absence = 0 AND removed = 0 GROUP BY shift.shift_id ORDER BY date ASC, time ASC;";
    private final String sqlGetShiftsIsUser = "SELECT user_id FROM employee_shift WHERE user_id = ? AND shift_id = ? AND removed = 0";
    private final String sqlSetStaffNumberOnShift = "UPDATE shift SET staff_number = ? WHERE shift_id = ?";
    private final String sqlGetUserFromShift = "SELECT * FROM employee_shift WHERE shift_id = ? AND user_id = ?";

    private final String sqlSetValidAbsence = "UPDATE employee_shift SET valid_absence = ? WHERE user_id = ? AND shift_id = ?;";

    private final String sqlGetAvailableShifts = "SELECT * FROM shift HAVING staff_number > " +
            "(SELECT COUNT(*) user_id FROM employee_shift WHERE employee_shift.shift_id = shift.shift_id)";
    private final String sqlWasEmployeeOnShift = "UPDATE employee_shift SET removed = 0 WHERE user_id = ? AND shift_id = ?;";
    private final String sqlGetUsersFromShift = "SELECT user_id, first_name, last_name, email,phonenumber,category,percentage_work" +
            " FROM user WHERE user_id IN(SELECT user_id FROM employee_shift WHERE shift_id = ?);";
    private final String sqlIsUserResponsible = "SELECT responsibility FROM employee_shift WHERE user_id = ? AND shift_id = ?;";
    private final String sqlSetUserResponsible = "UPDATE employee_shift SET responsibility = ? WHERE user_id = ? AND shift_id = ?;";

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
                prep.setDate(2, sqlDate);
                prep.setInt(3, shift.getType().getValue());
                prep.setBoolean(4, shift.isApproved());
                prep.setInt(5, shift.getDeptId());

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
                            prep.setInt(2, shift.getId());
                            prep.setBoolean(3,shiftUser.isResponsibility());
                            prep.setBoolean(4,shiftUser.isValid_absence());
                            prep.setBoolean(5,false);

                            if(prep.executeUpdate() == 0){
                                throw new SQLException("User info not added, rolled back");
                            }
                        }
                        out = shift.getId();
                        log.log(Level.INFO, "Created new shift with ID " + out);
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

    /**
     * Same as createNewShift, just optimized for multiple shifts
     * @param shifts An array containing the shifts to create
     * @return True if all shifts were created
     */
    public boolean bulkInsertShifts(List<Shift> shifts) {
        if (!setUp()) {
            return false;
        }
        try {
            startTransaction();
            conn = getConnection();
            prep = conn.prepareStatement(sqlCreateNewShift, PreparedStatement.RETURN_GENERATED_KEYS);

            for (Shift shift : shifts) {
                prep.setInt(1, shift.getStaffNumb());
                prep.setDate(2, shift.getDate());
                prep.setInt(3, shift.getType().getValue());
                prep.setBoolean(4, shift.isApproved());
                prep.setInt(5, shift.getDeptId());
                prep.addBatch();
            }
            int[] result = prep.executeBatch();
            ResultSet generatedKeys = prep.getGeneratedKeys();
            int i = 0;
            
            prep = conn.prepareStatement(sqlCreateNewShiftStaff);

            while (generatedKeys.next()) {
                Shift shift = shifts.get(i++);
                shift.setId(generatedKeys.getInt(1));


                ArrayList<ShiftUser> shiftUsers = shift.getShiftUsers();
                for (ShiftUser shiftUser : shiftUsers) {
                    prep.setInt(1, shiftUser.getUserId());
                    prep.setInt(2, shift.getId());
                    prep.setBoolean(3, shiftUser.isResponsibility());
                    prep.setBoolean(4, shiftUser.isValid_absence());
                    prep.setBoolean(5, false);
                    prep.addBatch();
                }
                log.log(Level.INFO, "Created new shift with ID " + shift.getId());
            }
            prep.executeBatch();
            return true;

        } catch (SQLException e) {
            rollbackStatement();
            log.log(Level.WARNING, "Issue with bulk creating new shifts, data rolled back", e);
        } finally {
            endTransaction();
            finallyStatement(prep);
        }
        return false;
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
                if(status >= 0){
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
                            res.getInt("valid_absence")));
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
                            shiftUsers, res.getBoolean("approved"));
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
                prep = conn.prepareStatement(sqlWasEmployeeOnShift);
                prep.setInt(1, shiftUser.getUserId());
                prep.setInt(2, shiftId);
                out = prep.executeUpdate() != 0;
                if (!out){
                    prep = conn.prepareStatement(addEmployeeToShift);
                    prep.setInt(1, shiftUser.getUserId());
                    prep.setInt(2, shiftId);
                    prep.setBoolean(3,shiftUser.isResponsibility());
                    prep.setBoolean(4,false);
                    prep.setBoolean(5,false);


                    out = prep.executeUpdate() != 0;
                }
            }
            catch (SQLException e){
                log.log(Level.WARNING, "Not able to get shift from shift ID = " + shiftId, e);
                e.printStackTrace();
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
                prep.setInt(1, shiftId);
                prep.setInt(2, userId);
                out = prep.executeUpdate() != 0;
                if(out && isUserResponsible(userId, shiftId)){
                    setResponsibleUser(shiftId, ShiftChangeUtil.findResponsibleUserForShift(shiftId).getId(), true);
                }
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

    private static final String sqlReplaceUser = "UPDATE employee_shift SET user_id = ?, valid_absence = 0, responsibility = FALSE WHERE user_id = ? AND shift_id = ?";

    /**
     * Replaces an user with another on a shift. Responsibility will not be transferred.
     * @param shiftId The shift we want to edit
     * @param oldUserId The ID of the user which already is on the shift
     * @param newUserId THe ID of the replacement user
     * @return True if the replacement was successful
     */

    public boolean replaceEmployeeOnShift(int shiftId, int oldUserId, int newUserId) {
        ShiftUser shiftUser = new ShiftUser(newUserId, null, null, false, 0);
        return addEmployeeToShift(shiftUser, shiftId) && deleteEmployeeFromShift(oldUserId, shiftId);
    }

    public ArrayList<ShiftUserBasic> getShiftWithUserId(int userId, Date date){
        ArrayList<ShiftUserBasic> out = new ArrayList<>();
        if(setUp()){
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(getShiftWithUserId);
                prep.setInt(1, userId);
                prep.setDate(2, date);
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

    public int getNumberOfShifts(int userId, Date startDate, Date endDate){
        int out = 0;
        ResultSet res = null;

        if(setUp()){
            try {
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetNumberOfShifts);

                prep.setInt(1,userId);
                prep.setDate(2,startDate);
                prep.setDate(3, endDate);

                res = prep.executeQuery();
                res.next();

                out = res.getInt(1);

            } catch (SQLException sqlE){
                log.log(Level.WARNING, "Error getting total number of hours for user with ID = " + userId, sqlE);
            } finally{
                endTransaction();
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
            "WHERE shift.date IS NULL OR shift.date BETWEEN ? AND ? " +
            "AND user.category != 0 " +
            "AND shift.approved = TRUE " +
            "GROUP BY user_id " +
            "UNION " +
            "SELECT *, 0 AS shifts_worked FROM user " +
            "WHERE category != 0 " +
            "AND shift.approved = TRUE " +
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
                    user.calculateTotalWorkHours();
                    users.add(user);
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "Could not get work hour list", e);
            } finally {
                finallyStatement(prep);
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
                log.log(Level.WARNING, "Error getting shifts with connected to availability and user", sqle);
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
                log.log(Level.WARNING, "Error editing number of staff number on shift "+shiftId, sqle);
            }
            finally {
                finallyStatement(prep);
            }
        }
        return status != 0;
    }

    private static final String sqlAnyShiftsInPeriod = "SELECT 1 FROM shift WHERE date BETWEEN ? AND ?";

    /**
     * Checks if there are any shifts registered in the given period
     * @param startDate The start of the period
     * @param endDate The end of the period
     * @return True if there is any shifts in the period
     */
    public boolean hasAnyShiftsInPeriod(LocalDate startDate, LocalDate endDate) {
        if (!setUp()) {
            log.log(Level.WARNING, "Failed to set up db connection");
            return true;
        }

        ResultSet result = null;

        try {
            prep = getConnection().prepareStatement(sqlAnyShiftsInPeriod);
            prep.setDate(1, Date.valueOf(startDate));
            prep.setDate(2, Date.valueOf(endDate));
            result = prep.executeQuery();
            return result.next();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Failed to check any shifts in period", e);
        } finally {
            finallyStatement(result, prep);
        }
        return true;
    }

    // Returns array with shifts that need more employees (shifts with not enough employees connected)
    public ArrayList<ShiftAvailable> getAvailableShifts(){
        ArrayList<ShiftAvailable> shiftList = new ArrayList<>();

        ResultSet res = null;

        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetAvailableShifts);
                res = prep.executeQuery();

                int index = 0;
                while(res.next()){
                    shiftList.add(new ShiftAvailable(
                            res.getInt("shift_id"),
                            res.getDate("date"),
                            Shift.ShiftType.valueOf(res.getInt("time")),"",
                            false,false));
                }

            } catch (SQLException sqlE){
                log.log(Level.WARNING, "Error getting shifts that need more employees", sqlE);
            } finally {
                finallyStatement(prep);
            }
        }
        return shiftList;
    }

    public ShiftUser getUserFromShift(int userId, int shiftId){
        ShiftUser shiftUser = null;
        if(setUp()){
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetUserFromShift);
                prep.setInt(2,userId);
                prep.setInt(1,shiftId);
                res = prep.executeQuery();
                if(res.next()){
                    UserDBManager userDb = new UserDBManager();
                    User user = userDb.getUserById(userId);
                    shiftUser = new ShiftUser(userId, user.getFirstName()+ " " +user.getLastName(),
                           user.getCategory(), res.getBoolean("responsibility"),
                            res.getInt("valid_absence"));
                }
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Issue getting user from shift", sqle);
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return shiftUser;
    }

    public boolean setValidAbsenceInt(int userId, int shiftId, int valid_absence){
        int result = 0;
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlSetValidAbsence);
                prep.setInt(1,valid_absence);
                prep.setInt(2,userId);
                prep.setInt(3,shiftId);
                result = prep.executeUpdate();
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Issue updating valid absence for user_id = "+userId, sqle);
            }
            finally {
                finallyStatement(prep);
            }
        }
        return result != 0;
    }

    public boolean setValidAbsence(int userId, int shiftId, boolean valid_absence){
        int result = 0;
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlSetValidAbsence);
                prep.setBoolean(1,valid_absence);
                prep.setInt(2,userId);
                prep.setInt(3,shiftId);
                result = prep.executeUpdate();
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Issue updating valid absence for user_id = "+userId, sqle);
            }
            finally {
                finallyStatement(prep);
            }
        }
        return result != 0;
    }
    public ArrayList<User> getUsersFromShift(int shiftId){
        ArrayList<User> out = new ArrayList<>();
        if(setUp()){
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetUsersFromShift);
                prep.setInt(1,shiftId);
                res = prep.executeQuery();
                while(res.next()){
                    out.add(new User(
                            res.getInt("user_id"),
                            res.getString("first_name"),
                            res.getString("last_name"),
                            null,null,
                            res.getString("email"),
                            res.getString("phonenumber"),
                            User.UserCategory.valueOf(res.getInt("category")),
                            res.getFloat("percentage_work")
                    ));
                }
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Issue getting users from shift with id = "+shiftId,sqle);
            }
            finally {
                finallyStatement(prep);
            }
        }
        return out;
    }

    /**
     * Attempts to mark the provided shifts as approved. (Shift plan approval)
     * @param shiftIds An array containing the IDs of the shifts to be approved
     * @return True if all the shifts was approved
     */
    public boolean approveShifts(int[] shiftIds) {
        if(!setUp()) {
            return false;
        }

        try {
            conn = getConnection();
            startTransaction();
            prep = conn.prepareStatement("UPDATE shift SET approved = TRUE WHERE shift_id = ?");
            for(Integer id : shiftIds) {
                prep.setInt(1, id);
                prep.addBatch();
            }
            int[] updateCounts = prep.executeBatch();

            // Check result
            for(int rowsUpdated : updateCounts) {
                if(rowsUpdated < 0) return false;
            }
            return true;
        } catch (SQLException e){
            rollbackStatement();
            log.log(Level.WARNING, "Issue approving shifts", e);
        }
        finally {
            endTransaction();
            finallyStatement(prep);
        }
        return false;
    }

    private boolean isUserResponsible(int userId, int shiftId){
        boolean isResponsible = false;
        if(setUp()){
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlIsUserResponsible);
                prep.setInt(1,userId);
                prep.setInt(2,shiftId);
                res = prep.executeQuery();
                if(res.next()){
                    isResponsible = res.getBoolean(1);
                }
            }catch (SQLException sqle){
                log.log(Level.WARNING, "Issue finding if user is responsible for shift_id = "+shiftId+" and userId = "+userId, sqle);
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return isResponsible;
    }
    public boolean setResponsibleUser(int shiftId, int userId, boolean isResponsible){
        boolean out = false;
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlSetUserResponsible);
                prep.setBoolean(1,isResponsible);
                prep.setInt(2,userId);
                prep.setInt(3,shiftId);
                out = prep.executeUpdate() != 0;
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Issue setting user responsible for shift_id = "+shiftId+" and userId = "+userId, sqle);
            }
            finally {
                finallyStatement(prep);
            }
        }
        if(out) ShiftChangeUtil.sendNewResponsibleChangeNotification(userId,shiftId);
        return out;
    }

}
