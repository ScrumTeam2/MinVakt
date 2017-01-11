package no.ntnu.stud.minvakt.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * Database class for connection between DBManager and mySQL
 */
public class DatabaseConnection {

    private final String username = "audunwar_scrum";
    private final String password = "c?6qUDAy";
    private final String databasedriver = "com.mysql.cj.jdbc.Driver";
    private final String databasename = "jdbc:mysql://mysql.stud.ntnu.no/audunwar_scrum_db";
    private Connection connection = null;


    public DatabaseConnection() {

        try {
            Class.forName(databasedriver);
            connection = DriverManager.getConnection(databasename,username,password);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Issue with database driver.");
        } catch (SQLException SQLe) {
            System.err.println("Issue with connecting to database.");
        }
    }

    /**
     * Gets the current connection
     * @return The current database connection
     */
    public Connection getConnection(){
        return connection;
    }


}