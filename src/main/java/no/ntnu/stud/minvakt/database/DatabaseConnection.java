package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.util.TravisUtil;

import java.lang.invoke.MethodHandles;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Database class for connection between DBManager and mySQL
 */
public class DatabaseConnection {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private final String username = TravisUtil.isTravis() ? "root" : "g_scrum02";
    private final String password = TravisUtil.isTravis() ? "" : "82dvE5og";
    private final String databasename = TravisUtil.isTravis() ? "jdbc:mysql://localhost/test" : "jdbc:mysql://mysql.stud.iie.ntnu.no/g_scrum02";

    private Connection connection = null;

    public DatabaseConnection() {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(databasename, username, password);
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, "Issue with database driver.", e);
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