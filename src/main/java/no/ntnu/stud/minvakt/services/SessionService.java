package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Session;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.UserDBManager;
import no.ntnu.stud.minvakt.util.rest.ErrorInfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.lang.invoke.MethodHandles;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handles user logging in/logging out
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
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Du er allerede logget inn.")).build();
        }

        UserDBManager userDBManager = new UserDBManager();
        User user = userDBManager.loginUser(identificator, password);
        if (user == null) {
            // No match in database, return error
            return Response.status(Response.Status.UNAUTHORIZED).entity(new ErrorInfo("Ugyldig brukernavn eller passord.")).build();
        }

        // Create session
        Session session = new Session();
        session.setUser(user);
        request.getSession().setAttribute("session", session);
        return Response.ok().entity(session.getUser()).build();
    }

    /**
     * Used to check if the client still is logged in
     * @param request
     * @return Status OK if user is logged in, status UNAUTHORIZED otherwise
     */
    @Path("/check")
    @GET
    public Response validateSession(@Context HttpServletRequest request) {
        if (request.getSession().getAttribute("session") != null) {
            Session session = (Session)request.getSession().getAttribute("session");
            return Response.ok(session.getUser()).build();
        }
        return Response.status(Response.Status.UNAUTHORIZED).build();
    }

    /**
     * Logs out an user (invalidates the session)
     * @param request
     * @return Status OK if success
     */
    @Path("/log_out")
    @GET
    public Response logOut(@Context HttpServletRequest request) {
        request.getSession().invalidate();
        return Response.ok().build();
    }
}
