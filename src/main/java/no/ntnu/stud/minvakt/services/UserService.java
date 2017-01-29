package no.ntnu.stud.minvakt.services;
import no.ntnu.stud.minvakt.controller.email.ForgotPass;
import no.ntnu.stud.minvakt.data.Session;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasic;
import no.ntnu.stud.minvakt.data.user.UserBasicList;
import no.ntnu.stud.minvakt.database.UserDBManager;
import no.ntnu.stud.minvakt.util.InputUtil;
import no.ntnu.stud.minvakt.util.rest.ErrorInfo;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Contains REST endpoints to get user information
 */
@Path("/user")
public class UserService extends SecureService{
    UserDBManager userDB = new UserDBManager();
    public UserService(@Context HttpServletRequest request) {
        super(request);
    }

    /**
     * @return ArrayList<UserBasics>
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<UserBasicList> getUserBasics() {
        if(getSession() == null) return null;

        return userDB.getUserBasics();
    }

    /**
     * @param category Integer: User category (0: Admin, 1: Nurse, 2: Healthworker, 3: Assistant)
     * @return If success: ArrayList<UserBasic>
     *         If not admin: Response UNAUTHORIZED
     */
    @Path("/category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<UserBasic> getUserBasicsWithCategory(@QueryParam("category")User.UserCategory category) {
        if(!getSession().isAdmin()) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }

        return userDB.getUserBasicsWithCategory(category);
    }

    /**
     * @param userId ID for given user
     * @return User object
     */
    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserBasics(@PathParam("userId") int userId) {
        if(getSession() == null) return null;

        return userDB.getUserById(userId);
    }

    /**
     * Changes password
     * @param oldPass - String with old password
     * @param newPass - String with new password
     * @return If success: Response ok,
     *         If not success: BAD_REQUEST
     */
    @POST
    @Path("/changepass")
    public Response changePassword(@FormParam("oldpass") String oldPass, @FormParam("newpass") String newPass){
        // Validate new password
        if(!InputUtil.validatePassword(newPass)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        int status = userDB.changePasswordUserId(Integer.toString(getSession().getUser().getId()), oldPass, newPass);

        if(status > 0){
            return Response.ok().entity("Password is changed").build();
        }
        else{
            return Response.status(Response.Status.BAD_REQUEST).entity("Issue arised, old password may be wrong").build();
        }
    }

    /**
     * @param email String email address
     * @return if success: Response.ok
     * TODO: Remove unused code?
     */
    @POST
    @Path("/forgottenpass")
    public Response sendNewPassword(@FormParam("email") String email){
        int status = ForgotPass.sendEmailWithNewPass(email);

        // Replaced detailed responses with a generic one. This way nobody can abuse this function to check for registered mails.
        return Response.ok().entity("Hvis e-posten er gyldig, har det blitt sendt et nytt passord.").build();
//        if(status < 0){
//            return Response.status(Response.Status.BAD_REQUEST).entity("Issue with request, mail may be wrong").build();
//        }
//        else if (status == 0){
//            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Error sending mail, password is changed, but mail not sent").build();
//        }
//        else{
//            return Response.ok().entity("Mail with new password sent!").build();
//        }
    }

    /**
     * Edits user
     * @param email String email address
     * @param phoneNumber String phone number
     * @return if success: response.ok
     *         if phone number is taken: BAD_REQUEST
     *         if email address is taken: BAD_REQUEST
     *         fail to edit user: Response.serverError
     */
    @POST
    @Path("/edituser")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editUser(@QueryParam("email") String email, @QueryParam("phone") String phoneNumber) {
        Session session = getSession();
        if(getSession() == null) return null;

        // Verify user data
        // Check that identifiers doesn't already exist
        if(userDB.isPhoneNumberTaken(phoneNumber)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Telefonnummeret er allerede i bruk")).build();
        }

        if(userDB.isEmailTaken(email)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("E-posten er allerede i bruk")).build();
        }

        // Insert into database
        int userInfo = userDB.changeUserInfoSimple(email,phoneNumber,getSession().getUser().getId());
        if(userInfo>-1) {
            //String json = "{\"Success. id\": \"" + email +  "\"}";
            String json = "{\"Success change. \": \"" + email + "\", \"phone\":\"" + phoneNumber+"\"}";
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        } else {
            log.log(Level.WARNING, "Failed to edit user: " + getSession().getUser().getId());
            return Response.serverError().build();
        }
    }
    @GET
    @Path("/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserForProfile(){
        return getSession().getUser();
    }
}
