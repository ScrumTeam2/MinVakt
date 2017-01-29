package no.ntnu.stud.minvakt.database;
import no.ntnu.stud.minvakt.data.Overtime;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by AnitaKristineAune on 12.01.2017.
 */
public class OvertimeDBManager extends DBManager{


    private final String sqlSetOvertime = "REPLACE INTO overtime VALUES(?,?,?,?,?)";
    private final String sqlApproveOvertime = "UPDATE overtime SET approved=1 WHERE user_id = ? AND shift_id = ?";
    private final String sqlDeleteOvertime = "DELETE FROM overtime WHERE user_id = ? AND shift_id = ? AND start_time = ?";

    private final String sqlGetUnapprovedOvertime = "SELECT * FROM overtime WHERE approved = 0";
    private final String sqlCountUnapproved = "SELECT COUNT(*) FROM overtime WHERE approved = 0";

    private final String getSqlGetOvertimeByUserId = "SELECT overtime.*, shift.date, shift.time FROM overtime NATURAL JOIN employee_shift JOIN shift ON (shift.shift_id = employee_shift.shift_id) WHERE overtime.user_id =?;";
    private final String sqlCountOvertimeUser = "SELECT COUNT(*) FROM overtime WHERE user_id = ?";
    private final String sqlGetMinutesByDate = "SELECT sum(minutes) AS minute_sum FROM overtime NATURAL JOIN employee_shift JOIN shift ON employee_shift.shift_id = shift.shift_id WHERE overtime.user_id = ? AND date BETWEEN ? AND ? AND shift.approved = TRUE";
    private final String sqlGetMinutes = "SELECT minutes FROM overtime WHERE user_id = ? AND shift_id = ? AND start_time = ?";
    private final String sqlGetOvertimeShift = "SELECT * FROM overtime WHERE user_id = ? AND shift_id = ?";



    Connection conn;
    PreparedStatement prep;

    public OvertimeDBManager(){
        super();
    }

    /**
     * Registers overtime/absence on a given user. (not an entire shift)
     * @param userId - employee ID
     * @param shiftId - shift ID
     * @param startTime - time of day
     * @param minutes - the amount of minutes worked extra/been absent
     * @return True if success
     */
    public boolean setOvertime(int userId, int shiftId, int startTime, int minutes){
        int out = 0;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlSetOvertime);

                prep.setInt(1,userId);
                prep.setInt(2,shiftId);
                prep.setInt(3, startTime);
                prep.setInt(4, minutes);
                prep.setBoolean(5, false);

                out = prep.executeUpdate();

            } catch (SQLException sqlE){
                log.log(Level.WARNING,"Error register overtime on user with ID = " + userId, sqlE);
            } finally {
                endTransaction();
                finallyStatement(prep);
            }
        }

        return out != 0;
    }


    /**
     * Deletes overtime from database
     * @param userId - employee ID
     * @param shiftId - shift ID
     * @param startTime
     * @return True if success
     */
    public boolean deleteOvertime(int userId, int shiftId, int startTime){
        int out = 0;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlDeleteOvertime);
                prep.setInt(1, userId);
                prep.setInt(2, shiftId);
                prep.setInt(3,startTime);

                out = prep.executeUpdate();
            } catch(SQLException sqlE){
                log.log(Level.WARNING, "Error deleting overtime for user with id = " + userId + " and shift id = " + shiftId, sqlE);
            } finally {
                endTransaction();
                finallyStatement(prep);
            }
        }
        return out != 0;
    }

    /**
     * For admin to approve overtime, sets approved to true in DB for given user on given shift
     * @param userId - employee ID
     * @param shiftId - shift ID
     * @return True if success
     */
     public boolean approveOvertime(int userId, int shiftId){
        int out = 0;

        if(setUp()){
            try {
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlApproveOvertime);
                prep.setInt(1, userId);
                prep.setInt(2, shiftId);

                out = prep.executeUpdate();

            } catch (SQLException sqlE) {
                log.log(Level.WARNING, "Error approving overtime for user with id = " + userId + " on shift with id = " + shiftId, sqlE);
            } finally {
                endTransaction();
                finallyStatement(prep);
            }
        }
        return out != 0;
    }

    /**
     * @param userId - employee ID
     * @return ArrayList<Overtime> - Overtime objects for user given by userId
     */
    public ArrayList<Overtime> getOvertimeListByUserId(int userId){
        Overtime overtimeObj;
        ArrayList<Overtime> timeList = new ArrayList<>();

        ResultSet res = null;

        if(setUp()){
            try{
                conn = getConnection();
                prep = conn.prepareStatement(getSqlGetOvertimeByUserId);
                prep.setInt(1,userId);

                res = prep.executeQuery();

                while(res.next()){

                    overtimeObj = new Overtime(
                            res.getInt("user_id"),
                            res.getInt("shift_id"),
                            res.getInt("start_time"),
                            res.getInt("minutes"),
                            res.getBoolean("approved"),
                            res.getDate("date"),
                            res.getInt("time"));
                    timeList.add(overtimeObj);
                }

            } catch (SQLException sqlE){
                log.log(Level.WARNING, "Error returning overtime for user with ID = " + userId, sqlE);
            } finally {
                finallyStatement(res, prep);
            }
        }
        return timeList;
    }

    /**
     * Fetched all unapproved overtime registered by employees
     * @return Overime[] - array of Overtime objects with the overtimes not yet approved by admin
     */
    public Overtime[] getUnapprovedOvertime(){
        Overtime overtimeObj;
        int rowCount = getRowCount();
        Overtime[] timeList = new Overtime[rowCount];

        ResultSet res = null;

        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetUnapprovedOvertime);

                res = prep.executeQuery();

                int index = 0;
                while(res.next()){

                    overtimeObj = new Overtime(
                            res.getInt("user_id"),
                            res.getInt("shift_id"),
                            res.getInt("start_time"),
                            res.getInt("minutes"),
                            res.getBoolean("approved"));
                    timeList[index] = overtimeObj;

                    index++;
                }

            } catch (SQLException sqlE){
                log.log(Level.WARNING, "Error returning unapproved overtime", sqlE);
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return timeList;
    }

    /**
     * @param userId - employee ID
     * @param shiftId - shift ID
     * @param startTime - time of day for worked overtime
     * @return int - minutes worked for a given user, shift and time of day
     */
    public int getMinutes(int userId, int shiftId, int startTime){
        int minutes = 0;
        ResultSet res = null;

        if(setUp()){
            try {
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetMinutes);

                prep.setInt(1, userId);
                prep.setInt(2, shiftId);
                prep.setInt(3, startTime);

                res = prep.executeQuery();
                res.next();

                minutes = res.getInt("minutes");

            } catch (SQLException sqlE){
                log.log(Level.WARNING,"Error getting minutes for user with id = " + userId, sqlE);
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return minutes;
    }

    /**
     * @param userId - employee ID
     * @param shiftId - shift ID
     * @return ArrayList<Overtime> - List of Overtime objects for the given shift
     */
     public ArrayList<Overtime> getOvertimeByShift(int userId, int shiftId){
        ArrayList<Overtime> overtimeList = new ArrayList<>();
        Overtime overtime = null;
        ResultSet res = null;

        if(setUp()){
            try {
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetOvertimeShift);

                prep.setInt(1, userId);
                prep.setInt(2, shiftId);

                res = prep.executeQuery();

                while(res.next()){
                    int startTime = res.getInt("start_time");
                    int minutes =res.getInt("minutes");
                    boolean approved = res.getBoolean("approved");
                    overtime = new Overtime(userId, shiftId,startTime,minutes, approved);
                    overtimeList.add(overtime);
                }


            } catch (SQLException sqlE){
                log.log(Level.WARNING,"Error getting minutes for user with id = " + userId, sqlE);
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return overtimeList;
    }

    /**
     * Calculates total minutes registered (aside from shifts) for a given period
     * @param userId - employee ID
     * @param fromDate - date start of period
     * @param toDate - date end of period
     * @return int - total minutes
     */
     public int getMinutesByDate(int userId, Date fromDate, Date toDate){
        int minutes = 0;
        ResultSet res = null;

        if(setUp()){
            try {
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetMinutesByDate);

                prep.setInt(1, userId);
                prep.setDate(2, fromDate);
                prep.setDate(3, toDate);

                res = prep.executeQuery();
                res.next();

                minutes = res.getInt("minute_sum");

            } catch (SQLException sqlE){
                log.log(Level.WARNING,"Error getting minutes for user with id = " + userId, sqlE);
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return minutes;
    }

    //helping method
    private int getRowCount() {
        int count = 0;
        ResultSet res = null;
        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlCountUnapproved);

                res = prep.executeQuery();
                while(res.next()){
                    count += res.getInt("COUNT(*)");
                }
            } catch (SQLException sqlE){
                log.log(Level.WARNING, "Error getting row count", sqlE);
            } finally{
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return count;
    }
}
