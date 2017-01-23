package no.ntnu.stud.minvakt.services;


import no.ntnu.stud.minvakt.data.shift.ShiftAvailable;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

@Path("/availability")
public class AvailabilityService {
    AvailabilityDBManager availabilityDB = new AvailabilityDBManager();

    @POST
    public Response setAvailability(@QueryParam("userId") int userId, @QueryParam("shiftId") int shiftId) {

        boolean ok = availabilityDB.setAvailability(userId, shiftId);
        if (!ok) {
            return Response.status(400).entity("Unable to register availability.").build();
        } else {
            return Response.status(200).build();
        }
    }

    /*
    @Path("/date")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShiftForRequestedDate(@Context HttpServletRequest request, @FormParam("dateSelection") String dateString ) {
        // Return if already logged in
        if (request.getSession() == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        //ArrayList<ShiftAvailable> out = availabilityDB.getAvailabilityForDate(dateString);
        //return out;
    }
    */
    /*
    @GET
    @Path("/{shiftId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailability(@PathParam("shiftId") int shiftId){
        ArrayList<Integer> out = availabilityDB.getAvailability(shiftId);
        if(out.isEmpty()){
            return Response.status(400).entity("Unable to find available employees.").build();
        }else {
            String json = "{\"id\": \"" + out + "\"}";
            //return Response.status(200).entity(json).build();
            return Response.ok(json, MediaType.APPLICATION_JSON).build();

        }
    }
    */
    //Gets available shifts for specific date
    @GET
    @Path("/{dateString}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftAvailable> getShiftsAvailable(@PathParam("dateString") String dateString) {
        ArrayList<ShiftAvailable> out = availabilityDB.getAvailabilityForDate(dateString);
        return out;
    }

    @DELETE
    @Path("/{shiftId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteAvailability(@QueryParam("brukerId") int brukerId, @PathParam("shiftId") int shiftId) {
        boolean isDeleted = availabilityDB.deleteAvailability(brukerId, shiftId);
        if (!isDeleted) {
            return Response.status(400).entity("Unable to delete availability.").build();
        } else {
            return Response.status(200).build();
        }
    }
}
