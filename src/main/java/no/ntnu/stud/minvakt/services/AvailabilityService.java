package no.ntnu.stud.minvakt.services;


import no.ntnu.stud.minvakt.database.AvailabilityDBManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Created by Marit on 11.01.2017.
 */
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

    @GET
    @Path("/{shiftId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailability(@PathParam("shiftId") int shiftId){
        ArrayList<Integer> out = availabilityDB.getAvailability(shiftId);
        if(out==null){
            return Response.status(400).entity("Unable to find available employees.").build();
        }else {
            return Response.status(200).entity(out).build();
        }
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
