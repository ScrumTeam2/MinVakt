package no.ntnu.stud.minvakt.services;
import no.ntnu.stud.minvakt.data.Session;
import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.ShiftUser;
import no.ntnu.stud.minvakt.data.ShiftUserBasic;
import no.ntnu.stud.minvakt.database.DBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

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

    public ShiftService(@Context HttpServletRequest request) {
        super(request);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createShift(Shift shift) {
        if(true){//getSession().isAdmin()) {
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
    @Path("/{shiftId}")
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
    public Response deleteEmployeeFromShift(@PathParam("userId") int userId, @PathParam("shiftId") int shiftId) {
        if (getSession() == null) return null;

        boolean statusOk = shiftDB.deleteEmployeeFromShift(userId, shiftId);
        if (statusOk) {
            return Response.status(200).build();
        } else {
            return Response.status(400).entity("Unable to delete employee").build();
        }
    }

    @GET
    @Path("/user/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserBasic> getUserBasicFromId() {
        return shiftDB.getShiftWithUserId(getSession().getUser().getId());
    }
}
