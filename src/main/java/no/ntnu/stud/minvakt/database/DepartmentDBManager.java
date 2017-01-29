package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.department.Department;
import no.ntnu.stud.minvakt.data.department.DepartmentUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Database layer class for departments
 */
public class DepartmentDBManager extends DBManager{
    Connection conn;
    PreparedStatement prep;

    private final String sqlGetDepartments = "SELECT * FROM department;";
    private final String sqlGetDepartment = "SELECT * FROM department WHERE dept_id = ?;";
    private final String sqlGetAvailableShiftDept = "SELECT COUNT(shift_id) as shift_count, staff_number, shift_id FROM shift WHERE shift.dept_id = ? HAVING staff_number > " +
            "(SELECT COUNT(user_id) FROM employee_shift WHERE employee_shift.shift_id = shift.shift_id);";
    private final String sqlGetShiftsWithUser = "SELECT COUNT(shift_id) as shift_count FROM employee_shift NATURAl JOIN shift WHERE shift.dept_id = ? AND user_id = ? " +
            "AND date >= CURDATE()";


    /**
     * Returns a list of the departments
     * @return ArrayList<Department> with Department objects
     */
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

    /**
     * Returns ArrayList with Departments - contains info about whether a department has available users...?
     * @param userId
     * @return
     */
    public ArrayList<DepartmentUser> getDepartmentsWithData(int userId){
        ArrayList<DepartmentUser> departments = new ArrayList<>();
        if(setUp()){
            ResultSet res = null;
            ResultSet res2 = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetDepartments);
                res = prep.executeQuery();
                boolean hasAvailable = false;
                boolean hasUser = false;
                while(res.next()){
                    int deptId = res.getInt("dept_id");
                    String name = res.getString("dept_name");
                    prep = conn.prepareStatement(sqlGetAvailableShiftDept);
                    prep.setInt(1,deptId);
                    res2 = prep.executeQuery();
                    if(res2.next()){
                        hasAvailable = res2.getInt("shift_count") > 0;
                    }
                    prep = conn.prepareStatement(sqlGetShiftsWithUser);
                    prep.setInt(1,deptId);
                    prep.setInt(2,userId);
                    res2 = prep.executeQuery();
                    if(res2.next()){
                        hasUser = res2.getInt("shift_count") > 0;
                    }
                    departments.add(new DepartmentUser(
                                    deptId, name, hasAvailable,hasUser
                            )
                    );
                }
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Issue getting departements", sqle);
            }
            finally {
                finallyStatement(res, prep);
                finallyStatement(res2);
            }
        }
        return departments;
    }


    /**
     * Returns department given department ID
     * @param deptId - ID of department
     * @return Department object
     */
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
