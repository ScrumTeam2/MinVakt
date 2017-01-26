package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

/**
 * Created by evend on 1/25/2017.
 */
public class DepartmentDBManager extends DBManager{
    Connection conn;
    PreparedStatement prep;

    private final String sqlGetDepartments = "SELECT * FROM department;";
    private final String sqlGetDepartment = "SELECT * FROM department WHERE dept_id = ?;";

    public ArrayList<Department> getDepartments(){
        ArrayList<Department> departments = new ArrayList<>();
        if(setUp()){
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetDepartments);
                res = prep.executeQuery();
                while(res.next()){
                    departments.add(new Department(
                            res.getInt("dept_id"),
                            res.getString("dept_name")
                            )
                    );
                }
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Issue getting departements", sqle);
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return departments;
    }
    public Department getDepartment(int deptId){
        Department dept = null;
        if(setUp()){
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetDepartment);
                prep.setInt(1,deptId);
                res = prep.executeQuery();
                if(res.next()){
                    dept = new Department(res.getInt("dept_id"),
                            res.getString("dept_name"));
                }
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Issue getting departement with id = "+deptId, sqle);
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return dept;
    }
}
