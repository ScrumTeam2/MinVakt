package no.ntnu.stud.minvakt.services;
import no.ntnu.stud.minvakt.data.*;
import no.ntnu.stud.minvakt.database.DBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by evend on 1/10/2017.
 */

/*
    Class for creating shifts
    Parameter date needs to be on correct format
 */
@Path("/user")
public class UserService {
    UserDBManager userDB = new UserDBManager();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<UserBasic> getUserBasics(Shift shift) {
        return userDB.getUserBasics();
    }
}
