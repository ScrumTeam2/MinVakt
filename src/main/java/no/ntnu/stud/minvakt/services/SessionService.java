package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Session;
import no.ntnu.stud.minvakt.data.User;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by Audun on 10.01.2017.
 */
@Path("session")
public class SessionService {
    @Path("/login")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response checkLogin(@Context HttpServletRequest request, @FormParam("identificator") String identificator, @FormParam("password") String password) {
        // Return if already logged in
        if(request.getSession().getAttribute("session") != null) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        // LoginHandler loginHandler = new LoginHandler();
        // if(!loginHandler.checkLoginEmail(identificator, password) && !loginHandler.checkLoginPhone(identificator, password)) {
        // // No match in database, return error
        // return;
        // }
        //

        // Create session
        Session session = new Session();
        session.setUser(new User());
        request.getSession().setAttribute("session", session);
        return Response.ok().entity(session.getUser()).build();
    }
}
