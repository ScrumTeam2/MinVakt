package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Session;
import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.database.UserDBManager;
import no.ntnu.stud.minvakt.util.ErrorInfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Audun on 10.01.2017.
 */
@Path("session")
public class SessionService {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkLogin(@Context HttpServletRequest request, @FormParam("identificator") String identificator, @FormParam("password") String password) {
        // Return if already logged in
        if (request.getSession().getAttribute("session") != null) {
            log.log(Level.INFO, "Session already exists!");
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        UserDBManager userDBManager = new UserDBManager();
        User user = userDBManager.loginUser(identificator, password);
        if (user == null) {
            // No match in database, return error
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Invalid credentials")).build();
        }

        // Create session
        Session session = new Session();
        session.setUser(user);
        request.getSession().setAttribute("session", session);
        return Response.ok().entity(session.getUser()).build();
    }
}
