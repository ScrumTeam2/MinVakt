package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Overtime;
import no.ntnu.stud.minvakt.database.OvertimeDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Marit on 20.01.2017.
 */

@Path("/overtime")
public class OvertimeService extends SecureService{
    OvertimeDBManager overtimeDBManager = new OvertimeDBManager();
    public OvertimeService(@Context HttpServletRequest request) {
        super(request);
    }


    //GET fetch not approved overtime (used by Admin)
    //getUnapporovedOvertime() return Overtime[]
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUnapprovedOvertime(){
        if(getSession().isAdmin()){
            Overtime[] overtime = overtimeDBManager.getUnapprovedOvertime();
            if(overtime==null){
                return Response.status(400).entity("Unable to find unapproved overtime.").build();
            }else{
                return Response.status(200).entity(overtime).build();
            }
        }else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not an admin").build();
        }
    }


    //POST set approved overtime (used by Admin)
    //approveOvertime(userId, shiftId) return true/false
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setApprovedOvertime(@QueryParam("userId") int userId, @QueryParam("shiftId") int shiftId){
        if(getSession().isAdmin()){
            boolean isApproved = overtimeDBManager.approveOvertime(userId, shiftId);
            if(!isApproved){
                return Response.status(400).entity("Unable to find unapproved overtime.").build();
            }else{
                return Response.status(200).build();
            }
        }else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not an admin").build();
        }
    }

    //POST register overtime (used by employee-user)
    //setOvertime(int userId, int shiftId, int startTime, int minutes) returnes true/false
    @POST
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setOvertime(Overtime overtime) {

        return null;
    }

    //GET fetch overtime for user (used by employee-user)
    //getOvertimeByUserId(int userId) returns Overtime[]
    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Overtime[] getOvertime(@PathParam("userId") int shiftId) {

        //if (getSession() == null) return null;
        return null;
    }
}
