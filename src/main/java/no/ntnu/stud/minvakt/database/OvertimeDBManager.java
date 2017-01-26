package no.ntnu.stud.minvakt.database;
import com.mysql.cj.api.mysqla.result.Resultset;
import no.ntnu.stud.minvakt.data.Overtime;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by AnitaKristineAune on 12.01.2017.
 */
public class OvertimeDBManager extends DBManager{


    private final String sqlSetOvertime = "INSERT INTO overtime VALUES(?,?,?,?,?)";
    private final String sqlApproveOvertime = "UPDATE overtime SET approved=1 WHERE user_id = ? AND shift_id = ?";
    private final String sqlDeleteOvertime = "DELETE FROM overtime WHERE user_id = ? AND shift_id = ? AND start_time = ?";

    private final String sqlGetUnapprovedOvertime = "SELECT * FROM overtime WHERE approved = 0";
    private final String sqlCountUnapproved = "SELECT COUNT(*) FROM overtime WHERE approved = 0";

    private final String getSqlGetOvertimeByUserId = "SELECT overtime.*, shift.date, shift.time FROM overtime NATURAL JOIN employee_shift JOIN shift ON (shift.shift_id = employee_shift.shift_id) WHERE overtime.user_id =?;";
    private final String sqlCountOvertimeUser = "SELECT COUNT(*) FROM overtime WHERE user_id = ?";
    private final String sqlGetMinutes = "SELECT sum(minutes) AS minute_sum FROM overtime NATURAL JOIN employee_shift JOIN shift ON employee_shift.shift_id = shift.shift_id WHERE overtime.user_id = ? AND date BETWEEN ? AND ? AND shift.approved = TRUE";


    Connection conn;
    PreparedStatement prep;

    public OvertimeDBManager(){
        super();
    }

    // Registers overtime on a given user. Returns true or false
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

    // For admin to approve overtime, sets approved to true in DB for given user on given shift
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

    // returns array with overtime for given user
    public ArrayList<Overtime> getOvertimeByUserId(int userId){
        Overtime overtimeObj;
        ArrayList<Overtime> timeList = new ArrayList<>();

        ResultSet res = null;

        if(setUp()){
            try{
                startTransaction();
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
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return timeList;
    }

    // Returns array of Overtime objects with the overtimes not yet approved
    public Overtime[] getUnapprovedOvertime(){
        Overtime overtimeObj = null;
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

    /*
    private int getRowCountUser(int userId) {
        int count = 0;
        ResultSet res = null;
        if(setUp()){
            try{
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlCountOvertimeUser);
                prep.setInt(1, userId);

                res = prep.executeQuery();

                prep.setInt(1, userId);
                while(res.next()){
                    count += res.getInt("COUNT(*)");
                }
            } catch (SQLException sqlE){
                log.log(Level.WARNING, "Error getting row count", sqlE);
            } finally{
                endTransaction();
                finallyStatement(prep);
            }
        }
        return count;
    }
    */

    public int getMinutes(int userId, Date fromDate, Date toDate){
        int minutes = 0;
        ResultSet res = null;

        if(setUp()){
            try {
                startTransaction();
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetMinutes);

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
}
