package no.ntnu.stud.minvakt.services;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

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
}
