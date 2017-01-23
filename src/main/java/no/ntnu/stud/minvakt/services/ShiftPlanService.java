package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.controller.shiftplan.ShiftPlanController;
import no.ntnu.stud.minvakt.data.shiftplan.ShiftPlan;
import no.ntnu.stud.minvakt.util.ErrorInfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;

/**
 * Created by Audun on 20.01.2017.
 *
 * REST endpoints of shift plan (turnus)
 */
@Path("shiftplan")
public class ShiftPlanService extends SecureService {

    public ShiftPlanService(@Context HttpServletRequest request) {
        super(request);
    }

    @POST
    @Path("/{startDate}")
    public Response getGeneratedShiftPlan(@PathParam("startDate") Date startDate, ShiftPlan plan) {
        plan.setStartDate(startDate.toLocalDate());
        ShiftPlanController controller = new ShiftPlanController(plan);
        if(!controller.verifyValidity()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorInfo("Det finnes allerede en eller flere vakter i perioden " + startDate + " til " + startDate.toLocalDate().plusWeeks(6)))
                    .build();
        }

        controller.calculateShifPlan();
        controller.insertShiftsIntoDatabase();
        return Response.ok(plan).build();
    }
}
