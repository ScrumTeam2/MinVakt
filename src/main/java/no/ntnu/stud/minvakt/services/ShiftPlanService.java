package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.controller.shiftplan.ShiftPlanController;
import no.ntnu.stud.minvakt.data.shiftplan.ShiftPlan;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.util.rest.ErrorInfo;
import no.ntnu.stud.minvakt.util.rest.IntArrayWrapper;
import org.json.JSONArray;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Created by Audun on 20.01.2017.
 *
 * REST endpoints of shift plan (turnus)
 */
@Path("shiftplan")
public class ShiftPlanService extends SecureService {
    private ShiftDBManager shiftDBManager = new ShiftDBManager();

    public ShiftPlanService(@Context HttpServletRequest request) {
        super(request);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{startDate}")
    public Response getGeneratedShiftPlan(@PathParam("startDate") Date startDate, ShiftPlan plan) {
        plan.setStartDate(startDate.toLocalDate());
        ShiftPlanController controller = new ShiftPlanController(plan);
        if (!controller.verifyValidity()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("Det finnes allerede en eller flere vakter i perioden " + startDate + " til " + startDate.toLocalDate().plusWeeks(6)))
                    .build();
        }

        controller.calculateShifPlan();
        controller.insertShiftsIntoDatabase();

        // Stupid code to return JSON array
        JSONArray array = new JSONArray();
        ArrayList<Integer> shiftIds = controller.getShiftIds();
        for(Integer id : shiftIds) {
            array.put(id);
        }
        return Response.ok(array.toString()).build();
    }

    @Path("/approve")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response approveShiftPlan(IntArrayWrapper container) {
        if(shiftDBManager.approveShifts(container.getContent())) {
            return Response.ok().build();
        } else {
            log.log(Level.WARNING, "Could not approve shifts");
            return Response.serverError().build();
        }
    }

}
