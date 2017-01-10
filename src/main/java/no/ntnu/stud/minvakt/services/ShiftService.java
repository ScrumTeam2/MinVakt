package no.ntnu.stud.minvakt.services;
import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.database.ShiftDBManager;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.Date
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by evend on 1/10/2017.
 */
@Path("/shift")
public class ShiftService {
    ShiftDBManager shiftDB = new ShiftDBManager();
    @POST
    @Path("/")
    public Response createShift(
                    @QueryParam("id") int id,
                    @QueryParam("date") java.util.Date date,
                    @QueryParam("type") int type,
                    @QueryParam("responsibility") boolean res,
                    @QueryParam("valdiAbsence") boolean validAb,
                    @QueryParam("userId") int userId,
                    @QueryParam("deptId") int deptId) {
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        //Shift shift = new Shift(id, sqlDate, type, res, validAb, userId, deptId);

    }

}
