package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.UserWorkInfo;

import javax.ejb.Local;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Database layer class for salary list
 */
public class SalaryDBManager extends DBManager {
    private static final String sqlGetAllWorkHours = 
            "SELECT user.user_id, user.first_name, user.last_name, COUNT(shift.shift_id) shifts_worked, SUM(overtime.minutes) overtime_minutes " +
            "FROM user " +
            "LEFT JOIN employee_shift es ON(es.user_id = user.user_id AND es.valid_absence != 1) " +
            "LEFT JOIN shift ON (shift.shift_id = es.shift_id AND shift.approved = TRUE AND shift.date BETWEEN ? AND ?) " +
            "LEFT JOIN overtime ON (overtime.user_id = user.user_id AND overtime.shift_id = shift.shift_id AND overtime.approved = TRUE) " +
            "WHERE es.removed = FALSE OR es.removed IS NULL " +
            "GROUP BY user.user_id " +
            "ORDER BY user.user_id";

    /**
     * Generates list with all work hours and user info for salary management
     * @return Map<Integer, UserWorkInfo>
     */
    public Map<Integer, UserWorkInfo> getAllWorkHours() {
        HashMap<Integer, UserWorkInfo> workHours = new HashMap<>();

        if (!setUp()) {
            return null;
        }

        PreparedStatement statement = null;
        ResultSet result = null;
        try {
            statement = getConnection().prepareStatement(sqlGetAllWorkHours);
            statement.setDate(1, Date.valueOf(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth())));
            statement.setDate(2, Date.valueOf(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth())));
            result = statement.executeQuery();

            while (result.next()) {
                int userId = result.getInt("user_id");
                String firstName = result.getString("first_name");
                String lastName = result.getString("last_name");
                int shiftsWorked = result.getInt("shifts_worked");
                int minutesOvertime = result.getInt("overtime_minutes");

                workHours.put(userId, new UserWorkInfo(userId, firstName, lastName, shiftsWorked, minutesOvertime));
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Failed to get work hours", e);
            return null;
        } finally {
            finallyStatement(result, statement);
        }
        return workHours;
    }
}
