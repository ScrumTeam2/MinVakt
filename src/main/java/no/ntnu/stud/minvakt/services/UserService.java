package no.ntnu.stud.minvakt.services;
import no.ntnu.stud.minvakt.data.user.User;
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

    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserBasics(@PathParam("userId") int userId) {
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
            return Response.notModified().entity("Issue arised, old password may be wrong").build();
        }
    }


}
