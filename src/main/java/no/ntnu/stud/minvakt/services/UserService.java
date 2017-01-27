package no.ntnu.stud.minvakt.services;
import no.ntnu.stud.minvakt.controller.email.ForgotPass;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasic;
import no.ntnu.stud.minvakt.data.user.UserBasicList;
import no.ntnu.stud.minvakt.database.UserDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Created by evend on 1/10/2017.
 */

/*
 */
@Path("/user")
public class UserService extends SecureService{
    UserDBManager userDB = new UserDBManager();
    public UserService(@Context HttpServletRequest request) {
        super(request);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<UserBasicList> getUserBasics() {
        if(getSession() == null) return null;

        return userDB.getUserBasics();
    }
    @Path("/category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<UserBasic> getUserBasicsWithCategory(@QueryParam("category")User.UserCategory category) {
        if(!getSession().isAdmin()) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }

        return userDB.getUserBasicsWithCategory(category);
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserBasics(@PathParam("userId") int userId) {
        if(getSession() == null) return null;

        return userDB.getUserById(userId);
    }

    @POST
    @Path("/changepass")
    public Response changePassword(@FormParam("oldpass") String oldPass, @FormParam("newpass") String newPass){
        int status = userDB.changePasswordUserId(Integer.toString(getSession().getUser().getId()), oldPass, newPass);

        if(status > 0){
            return Response.ok().entity("Password is changed").build();
        }
        else{
            return Response.status(Response.Status.BAD_REQUEST).entity("Issue arised, old password may be wrong").build();
        }
    }
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
    @GET
    @Path("/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserForProfile(){
        return getSession().getUser();
    }
}
