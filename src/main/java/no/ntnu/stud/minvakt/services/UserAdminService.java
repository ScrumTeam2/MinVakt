package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.controller.email.Mail;
import no.ntnu.stud.minvakt.data.Session;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.util.rest.ErrorInfo;
import no.ntnu.stud.minvakt.database.UserDBManager;
import no.ntnu.stud.minvakt.util.InputUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Level;

/**
 * Contains REST endpoints for manipulating users
 */
@Path("admin")
public class UserAdminService extends SecureService {
    private UserDBManager userDBManager = new UserDBManager();

    public UserAdminService(@Context HttpServletRequest request) {
        super(request);
    }

    /** Creates a new user from a User object
     * @param user - User object of the user to be created
     * @return  if phoneNumber taken: Response BAD_REQUEST
     *          if email taken: Response BAD_REQUEST
     *          if successful: Response OK
     *          else: Response SERVER_ERROR
     */
    @POST
    @Path("/createuser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addUser(User user) {
        if(!getSession().isAdmin()) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }

        // Verify user data
        Response errorResponse = InputUtil.validateUser(user);
        if (errorResponse != null) return errorResponse;

        // Check that identifiers doesn't already exist
        if(userDBManager.isPhoneNumberTaken(user.getPhoneNumber())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Telefonnummeret er allerede i bruk")).build();
        }

        if(userDBManager.isEmailTaken(user.getEmail())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("E-posten er allerede i bruk")).build();
        }

        // Insert into database
        Object[] userInfo = userDBManager.createNewUser(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getCategory(), user.getWorkPercentage(),
                user.getDeptId());
        user.setId((int)userInfo[0]);

        if(user.getId() > 0) {
            String json = "{\"id\": \"" + user.getId() + "\", \"password\":\"" + userInfo[1]+"\"}";
            Mail.sendMail(user.getEmail(), "Du er registrert som bruker hos MinVakt", "Ditt passord er: "+userInfo[1]);
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } else {
            log.log(Level.WARNING, "Failed to insert user: " + user);
            return Response.serverError().build();
        }
    }

    /** Edits a user with a User object
     * @param user - User object of the user
     * @return  if new phoneNumber taken: Response BAD_REQUEST
     *          if new email taken: Response BAD_REQUEST
     *          if successful: Response OK
     *          else: Response SERVER_ERROR
     */
    @POST
    @Path("/edituser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editUser(User user) {
        Session session = getSession();
        if(!session.isAdmin()) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }

        // Verify user data
        Response errorResponse = InputUtil.validateUser(user);
        if (errorResponse != null) return errorResponse;

        // Check that identifiers doesn't already exist
        if(userDBManager.isPhoneNumberTaken(user.getPhoneNumber())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Telefonnummeret er allerede i bruk")).build();
        }

        if(userDBManager.isEmailTaken(user.getEmail())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("E-posten er allerede i bruk")).build();
        }

        // Insert into database
        int userInfo = userDBManager.changeUserInfo(user);
        /*Object[] userInfo = userDBManager.changeUserInfo(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getCategory(), user.getWorkPercentage(),
                user.getDeptId());
        user.setId((int)userInfo[0]);*/
        if(userInfo>-1) {
            String json = "{\"Success. id\": \"" + user.getId() + "\"}";
            //Mail.sendMail(user.getEmail(), "Brukerinfo oppdatert", " brukerId: "+user.getId());
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } else {
            log.log(Level.WARNING, "Failed to edit user: " + user);
            return Response.serverError().build();
        }
    }

    /** Deletes a user from database, should only be used for testing
     * @param userId - the ID of the user who should be deleted
     * @return if not admin: Response BAD_REQUEST
     *         if deleted: returns True
     *         else: returns False
     */
    @DELETE
    @Path("/deleteuser/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Boolean deleteUser(@PathParam("userId") int userId) {
        if(!getSession().isAdmin()) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }

        boolean isDeleted = userDBManager.deleteUser(userId);
        if(!isDeleted)log.log(Level.WARNING, "Failed to delete user: " + userId);
        return isDeleted;
    }
}
