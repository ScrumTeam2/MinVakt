package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Department;
import no.ntnu.stud.minvakt.database.DepartmentDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

/**
 * Created by evend on 1/25/2017.
 */
@Path("/department")
public class DepartmentService extends SecureService{

    public DepartmentService(@Context HttpServletRequest request) {
        super(request);
    }

    DepartmentDBManager deptDB = new DepartmentDBManager();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{deptId}")
    public Department getDepartment(@PathParam("deptId") int deptId){
        return deptDB.getDepartment(deptId);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<Department> getDepartments(){
        return deptDB.getDepartments();
    }
}
