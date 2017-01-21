package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Session;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.util.ErrorInfo;
import no.ntnu.stud.minvakt.database.UserDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;

/**
 * Created by Audun on 10.01.2017.
 */
@Path("admin")
public class UserAdminService extends SecureService {
    public UserAdminService(@Context HttpServletRequest request) {
        super(request);
    }

    @POST
    @Path("/createuser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        Session session = getSession();
        if(!session.isAdmin()) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }

        // Verify user data

        // TODO: Better verification of mail
        System.out.println(user);
        if(user.getEmail() == null || user.getEmail().isEmpty()) {
            return Response.ok(new ErrorInfo("Invalid mail")).build();
        }

        if(user.getFirstName() == null || user.getFirstName().isEmpty()) {
            return Response.ok(new ErrorInfo("Invalid first name")).build();
        }

        if(user.getLastName() == null || user.getLastName().isEmpty()) {
            return Response.ok(new ErrorInfo("Invalid last name")).build();
        }

        // TODO: Better verification of phone number?
        if(user.getPhoneNumber() == null || user.getPhoneNumber().isEmpty()) {
            return Response.ok(new ErrorInfo("Invalid phone number")).build();
        }

        // TODO: Verify user category

        // Insert into database
        UserDBManager userDBManager = new UserDBManager();
        Object[] userInfo = userDBManager.createNewUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getCategory().getValue());
        user.setId((int)userInfo[0]);
        if(user.getId() > 0) {
            String json = "{\"id\": \"" + user.getId() + "\", \"password\":\"" + userInfo[1]+"\"}";
            System.out.println(json);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }
        log.log(Level.WARNING, "Failed to insert user: " + user);
        return Response.serverError().build();
    }
    @DELETE
    @Path("/deleteuser/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean deleteUser(@PathParam("userId") int userId) {
        Session session = getSession();
        if(!session.isAdmin()) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }

        UserDBManager userDBManager = new UserDBManager();
        boolean isDeleted = userDBManager.deleteUser(userId);
        if(!isDeleted)log.log(Level.WARNING, "Failed to delete user: " + userId);
        return isDeleted;
    }
}
