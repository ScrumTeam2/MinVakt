package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.util.TravisUtil;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Database class for connection between DBManager and MySQL
 */
public class DatabaseConnection {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private Connection connection = null;

    public DatabaseConnection() {
        try {
            connection = DatabaseConfiguration.getConnection();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Issue with connecting to database.", e);
        }
    }

    /**
     * Gets the current connection
     *
     * @return The current database connection
     */
    public Connection getConnection() {
        return connection;
    }
}