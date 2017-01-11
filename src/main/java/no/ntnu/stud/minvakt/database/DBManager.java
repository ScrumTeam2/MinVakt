package no.ntnu.stud.minvakt.database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.*;


/**
 * Default abstract class for working with database.
 */
public abstract class DBManager{
    private Connection connection;
    private DatabaseConnection c;

    protected boolean setUp(){
        try {
            c = new DatabaseConnection();
            connection = c.getConnection();
        } catch (Exception e) {
            System.err.println("Connecting to database failed.");
            closeConnection();
            return false;
        }
        return !(connection == null);
    }
    protected void closeConnection(){
        try {
            if(connection != null)DbUtils.closeQuietly(connection);
        }
        catch (Exception e){
            System.err.println("Problem with closing connection");
            e.printStackTrace();
        }
    }
    protected void startTransaction(){
        try{
            if(connection.getAutoCommit()){
                connection.setAutoCommit(false);
            }
        }
        catch (SQLException sqle){
            System.err.println("Issue with setting autocommit false");
        }

    }
    protected void endTransaction(){
        try{
            if(connection.getAutoCommit()){
                connection.commit();
                connection.setAutoCommit(true);
            }
        }
        catch (SQLException sqle){
            System.err.println("Issue with setting autocommit false");
        }
    }

    protected void rollbackStatement() {
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException ee) {
            System.err.println("Rollback Statement failed");
        }
    }


    protected void finallyStatement(ResultSet res, PreparedStatement prep) {
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
                connection.setAutoCommit(true);
            }
            if (res != null) res.close();
            if (prep != null) prep.close();
        } catch (SQLException sqle) {
            System.err.println("Finally Statement failed");
            sqle.printStackTrace();
        }
        closeConnection();
    }
    protected void finallyStatement(PreparedStatement prep) {
        try {
            if (!connection.getAutoCommit()) {
                connection.commit();
                connection.setAutoCommit(true);
            }
            if (prep != null) prep.close();
        } catch (SQLException sqle) {
            System.err.println("Finally Statement failed");
            sqle.printStackTrace();
        }
        closeConnection();
    }

    protected Connection getConnection() { return connection; }

}