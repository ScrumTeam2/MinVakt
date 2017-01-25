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
        return userDB.getUserBasics();
    }
    @Path("/category")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<UserBasic> getUserBasicsWithCategory(@QueryParam("category")User.UserCategory category) {
        return userDB.getUserBasicsWithCategory(category);
    }

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserBasics(@PathParam("userId") int userId) {
        return userDB.getUserById(userId);
    }

    @POST
    @Path("/changepass")
    public Response changePassword(@FormParam("oldpass") String oldPass, @FormParam("newpass") String newPass){
        System.out.println("Oldpass = "+oldPass+" Newpass = "+newPass);
        int status = userDB.changePasswordUserId(Integer.toString(getSession().getUser().getId()), oldPass, newPass);
        System.out.println(status);
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
        if(status < 0){
            return Response.status(Response.Status.BAD_REQUEST).entity("Issue with request, mail may be wrong").build();
        }
        else if (status == 0){
            return Response.status(Response.Status.EXPECTATION_FAILED).entity("Error sending mail, password is changed, but mail not sent").build();
        }
        else{
            return Response.ok().entity("Mail with new password sent!").build();
        }
    }
    @GET
    @Path("/profile")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserForProfile(){
        return getSession().getUser();
    }


}
