package no.ntnu.stud.minvakt.util;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by Audun on 12.01.2017.
 */
public class QueryUtil {
    /**
     * Gets the generated key (usually AUTO_INCREMENT id) from a Statement.
     *
     * @param statement The active statement
     * @return The generated key, or -1 if error.
     */
    public static int getGeneratedKeys(Statement statement) {
        int generatedKey = -1;
        try (ResultSet result = statement.getGeneratedKeys()) {
            if (result.next()) {
                generatedKey = result.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return generatedKey;
    }
}
