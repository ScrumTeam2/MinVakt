package no.ntnu.stud.minvakt.services;

import jersey.repackaged.com.google.common.collect.Lists;
import no.ntnu.stud.minvakt.controller.shiftplan.ShiftPlanController;
import no.ntnu.stud.minvakt.data.*;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;
import no.ntnu.stud.minvakt.util.AvailableUsersUtil;
import no.ntnu.stud.minvakt.util.ShiftChangeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by evend on 1/10/2017.
 */

/*
    Class for creating shifts
    Parameter date needs to be on correct format
 */
@Path("/shift")
public class ShiftService extends SecureService{
    ShiftDBManager shiftDB = new ShiftDBManager();
    UserDBManager userDB = new UserDBManager();

    public ShiftService(@Context HttpServletRequest request) {
        super(request);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createShift(Shift shift) {
        if(getSession().isAdmin()) {
            int shiftId = shiftDB.createNewShift(shift);
            shift.setId(shiftId);
            if (shiftId < 0) {
                return Response.status(Response.Status.BAD_REQUEST).entity("Unable to create new shift.").build();
            } else {
                //TODO: Add shiftId to response
                String json = "{\"id\": \"" + shiftId + "\"}";
                return Response.ok(json, MediaType.APPLICATION_JSON).build();
            }
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not an admin").build();
        }
    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserAvailability> getShifts(@QueryParam("daysForward") int daysForward,
                                                      @QueryParam("date") Date date){
        if(date == null) date = new Date(System.currentTimeMillis());
        return shiftDB.getShifts(daysForward, getSession().getUser().getId(), date);
    }

    @DELETE
    @Path("/{shiftId}")
    public Response deleteShift(@PathParam("shiftId") int id) {
        if(getSession().isAdmin()) {
            boolean isDeleted = shiftDB.deleteShift(id);
            if (!isDeleted) {
                return Response.status(400).entity("Unable to delete shift.").build();
            } else {
                return Response.status(200).build();
            }
        }
        else{
            return Response.status(Response.Status.UNAUTHORIZED).entity("User is not an admin").build();
        }
    }

    @GET
    @Path("/{shiftId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Shift getShift(@PathParam("shiftId") int shiftId) {
        if (getSession() == null) return null;

        return shiftDB.getShift(shiftId);
    }

    /*
        Parameters:

     */
    @POST
    @Path("/{shiftId}/user/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addEmployeeToShift(ShiftUser shiftUser, @PathParam("shiftId") int shiftId) {
        if(getSession() == null) return null;

        boolean statusOk = shiftDB.addEmployeeToShift(shiftUser, shiftId);
        if (statusOk) {
            return Response.status(200).build();
        } else {
            return Response.status(400).entity("Unable to add employee").build();
        }
    }

    @DELETE
    @Path("/{shiftId}/user/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteEmployeeFromShift(@PathParam("userId") int userId, @PathParam("shiftId") int shiftId,
                                            @QueryParam("findNewEmployee") boolean findNewEmployee) {
        boolean statusOk = false;
        if(findNewEmployee) {
            statusOk = shiftDB.deleteEmployeeFromShift(userId, shiftId);

        }
        else {
            statusOk = ShiftChangeUtil.findNewUserToShift(shiftId, userId);
        }
        if (statusOk) {
            return Response.status(200).build();
        } else {
            return Response.status(400).entity("Unable to delete employee").build();
        }
    }

    @GET
    @Path("/user/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserBasic> getUserBasicFromSession(@QueryParam("date") Date date) {
        if(date == null)date = new Date(System.currentTimeMillis());
        return shiftDB.getShiftWithUserId(getSession().getUser().getId(), date);
    }

//    /**
//     * Generates a list of possible candidates for a specific shift
//     * @param shiftId
//     * @return A Response containing an array of Users
//     */
//    @GET
//    @Path("/{shiftId}/possiblecandidates")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getPossibleCandidates(@PathParam("shiftId") int shiftId) {
//        if (getSession() == null || !getSession().isAdmin()) {
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
//
//        Shift shift = new ShiftDBManager().getShift(shiftId);
//        if (shift == null) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//
//        ShiftPlanController controller = new ShiftPlanController(shift);
//        ArrayList<UserBasicWorkHours> candidates = controller.getPossibleCandidates();
//
//        // Add the candidates to db (admin can change these)
//        controller.savePossibleCandidates();
//
//        GenericEntity<List<UserBasicWorkHours>> entity =
//                new GenericEntity<List<UserBasicWorkHours>>(Lists.newArrayList(candidates)) {
//                };
//
//
//        return Response.ok().entity(entity).build();
//    }

    @GET
    @Path("user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserBasic> getUserBasicFromId(@PathParam("userId") int userId){
        //if(getSession().isAdmin()){
            return shiftDB.getShiftWithUserId(userId, new Date(System.currentTimeMillis()));
        //}
    }
    @POST
    @Path("/{shiftId}")
    public Response setStaffNumberOnShift(@PathParam("shiftId") int shiftId,
                                          @QueryParam("staffNumber") int staffNumber){
        boolean isOk = shiftDB.setStaffNumberOnShift(shiftId,staffNumber);
        if(isOk){
            return Response.ok().entity("Staff number on shift "+shiftId+ " was edited to "+staffNumber).build();
        }
        else{
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
