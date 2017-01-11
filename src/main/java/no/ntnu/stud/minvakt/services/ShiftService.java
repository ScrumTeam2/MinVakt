package no.ntnu.stud.minvakt.services;
import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.database.ShiftDBManager;

import javax.ws.rs.*;
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
public class ShiftService {
    ShiftDBManager shiftDB = new ShiftDBManager();
    @POST
    @Path("/")
    public Response createShift(
                    @QueryParam("date") java.util.Date date,
                    @QueryParam("type") int type,
                    @QueryParam("responsibility") boolean res,
                    @QueryParam("valdiAbsence") boolean validAb,
                    @QueryParam("userId") int userId,
                    @QueryParam("deptId") int deptId) {
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        //default id = -1, will change after created
        Shift shift = new Shift(-1, sqlDate, type, res, validAb, userId, deptId);
        int shiftId = shiftDB.createNewShift(shift);
        shift.setId(shiftId);
        if(shiftId < 0){
            return Response.status(400).entity("Unable to create new shift.").build();
        }
        else {
            //TODO: Add shiftId to response
            String json = "{\"id\": \""+shiftId+"\"}";
            return Response.ok(json,MediaType.APPLICATION_JSON).build();
        }
    }
    @DELETE
    @Path("/")
    public Response deleteShift(@QueryParam("id") int id) {
        boolean isDeleted = shiftDB.deleteShift(id);

        if(!isDeleted){
            return Response.status(400).entity("Unable to delete shift.").build();
        }
        else {
            //TODO: Add shiftId to response
            return Response.status(200).build();
        }
    }

}
