package no.ntnu.stud.minvakt.database;

import org.apache.commons.dbutils.DbUtils;

import java.lang.invoke.MethodHandles;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Default abstract class for working with database.
 */
public abstract class DBManager{
    protected static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    private Connection connection;
    private DatabaseConnection c;

    /**
     * Sets up connection with database
     * @return True if success
     */
    protected boolean setUp(){
        try {
            c = new DatabaseConnection();
            connection = c.getConnection();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Connecting to database failed.", e);
            closeConnection();
            return false;
        }
        return connection != null;
    }

    /**
     * Closes the connection with database
     */
    protected void closeConnection(){
        try {
            if(connection != null)DbUtils.closeQuietly(connection);
        }
        catch (Exception e){
            log.log(Level.SEVERE, "Problem with closing connection", e);
        }
    }

    /**
     * Starts transaction with database
     */
    protected void startTransaction(){
        try{
            if(connection.getAutoCommit()){
                connection.setAutoCommit(false);
            }
        }
        catch (SQLException e){
            log.log(Level.SEVERE, "Issue with setting autocommit false", e);
        }

    }

    /**
     * Ends transaction with database
     */
    protected void endTransaction(){
        try{
            if(!connection.getAutoCommit()){
                connection.commit();
                connection.setAutoCommit(true);
            }
        }
        catch (SQLException e){
            log.log(Level.SEVERE, "Issue with setting autocommit false", e);
        }
    }

    /**
     * Rollbacks database
     */
    protected void rollbackStatement() {
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Rollback Statement failed", e);
        }
    }


    /**
     * Closes database connection
     * @param res - The ResultSet to be closed
     * @param prep - SQL statement
     */
    protected void finallyStatement(ResultSet res, PreparedStatement prep) {
        try {
            if (!connection.isClosed() && !connection.getAutoCommit()) {
                connection.commit();
                connection.setAutoCommit(true);
            }
            if (res != null) res.close();
            if (prep != null) prep.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Finally Statement failed", e);
        }
        closeConnection();
    }

    /**
     * Closes database connection
     * @param res - The ResultSet to be closed
     */
    protected void finallyStatement(ResultSet res) {
        try {
            if (res != null) res.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Finally Statement failed", e);
        }
        closeConnection();
    }

    /**
     * Closes database connection
     * @param prep - SQL statement
     */
    protected void finallyStatement(PreparedStatement prep) {
        try {
            if (!connection.isClosed() && !connection.getAutoCommit()) {
                connection.commit();
                connection.setAutoCommit(true);
            }
            if (prep != null) prep.close();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Finally Statement failed", e);
        }
        closeConnection();
    }

    protected Connection getConnection() { return connection; }

}