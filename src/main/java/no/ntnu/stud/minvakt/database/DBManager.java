package no.ntnu.stud.minvakt.database;

import org.apache.commons.dbutils.DbUtils;

import java.sql.*;


/**
 * Default abstract class for working with database.
 */
public abstract class DBManager{
    private Connection connection;
    private Statement scentence;
    private DatabaseConnection c;

    protected boolean connected(){
        return scentence != null;
    }
    protected boolean setUp(){
        try {
            c = new DatabaseConnection();
            connection = c.getConnection();
            scentence = connection.createStatement();
        } catch (Exception e) {
            System.err.println("Connecting to database failed.");
            closeConnection();
            return false;
        }
        return !(connection == null || scentence == null);
    }
    protected void closeConnection(){
        try {
            if(scentence != null)DbUtils.closeQuietly(scentence);
            if(connection != null)DbUtils.closeQuietly(connection);
        }
        catch (Exception e){
            System.err.println("Problem with closing connection");
            e.printStackTrace();
        }
    }
    protected void turnOffAutocommit(){
        try{
            if(connection.getAutoCommit() == true){
                connection.setAutoCommit(false);
            }
        }
        catch (SQLException sqle){
            System.err.println("Issue with setting autocommit false");
        }

    }

    public void rollbackStatement() {
        try {
            if (!connection.getAutoCommit()) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException ee) {
            System.err.println("Rollback Statement failed");
        }
    }


    public void finallyStatement(ResultSet res, PreparedStatement prep) {
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

    protected Statement getScentence() {
        return scentence;
    }

    protected Connection getConnection() { return connection; }

}