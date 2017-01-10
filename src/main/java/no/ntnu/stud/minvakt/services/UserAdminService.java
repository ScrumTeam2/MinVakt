package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Session;
import no.ntnu.stud.minvakt.data.User;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

/**
 * Created by Audun on 10.01.2017.
 */
@Path("admin")
public class UserAdminService extends SecureService {
    @POST
    @Path("/createuser")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addUser(@Context SecurityContext sc, User user) {
        sc.isUserInRole("admin");
        Session session = getSession();
        if(!session.isAdmin()) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.FORBIDDEN);
        }

        // TODO: Verify user data, insert into db
        // LoginHandler h = new LoginHandler();
        // h.createNewAnsatt(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber() ...);
    }
}
