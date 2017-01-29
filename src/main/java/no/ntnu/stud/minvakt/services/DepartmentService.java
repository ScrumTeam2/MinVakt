package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.department.Department;
import no.ntnu.stud.minvakt.data.department.DepartmentUser;
import no.ntnu.stud.minvakt.database.DepartmentDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Contains REST endpoints for departments
 */
@Path("/department")
public class DepartmentService extends SecureService{

    public DepartmentService(@Context HttpServletRequest request) {
        super(request);
    }

    DepartmentDBManager deptDB = new DepartmentDBManager();

    /**
     * @param deptId - the Id of the department
     * @return department object
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{deptId}")
    public Department getDepartment(@PathParam("deptId") int deptId){
        if(getSession() == null) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }
        return deptDB.getDepartment(deptId);
    }

    /**Fetches delimited information about employees from a deparment,
     * in order to show information about employees and shifts in calendar for a spesific department
     * @return ArrayList of DepartmentUser objects
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/withData")
    public ArrayList<DepartmentUser> getDepartmentsWithData(){
        if(getSession() == null) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }
        return deptDB.getDepartmentsWithData(getSession().getUser().getId());
    }

    /**
     * @return ArrayList with Department objects with all departments
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Department> getDepartments(){
        if(getSession() == null) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }
        return deptDB.getDepartments();
    }
}
