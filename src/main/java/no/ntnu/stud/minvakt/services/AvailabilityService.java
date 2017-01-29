package no.ntnu.stud.minvakt.services;


import no.ntnu.stud.minvakt.data.*;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shift.ShiftUserAvailability;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.UserAvailableShifts;
import no.ntnu.stud.minvakt.data.user.UserBasicWorkHours;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;
import no.ntnu.stud.minvakt.util.AvailableUsersUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import no.ntnu.stud.minvakt.data.shift.ShiftAvailable;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Contains REST endpoints for availability
 */
@Path("/availability")
public class AvailabilityService extends SecureService {
    AvailabilityDBManager availabilityDB = new AvailabilityDBManager();

    public AvailabilityService(@Context HttpServletRequest request) {
        super(request);
    }

    // Only used in tests, disabled annotations
    //@POST
    public Response setAvailability(/*@QueryParam("userId")*/ int userId, /*@QueryParam("shiftId")*/ int shiftId) {

        boolean ok = availabilityDB.setAvailability(userId, shiftId);
        if (!ok) {
            return Response.status(400).entity("Unable to register availability.").build();
        } else {
            return Response.status(200).build();
        }
    }

    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public UserAvailableShifts getAvailabilityUser() {
        if (getSession() == null) return null;
        int id = getSession().getUser().getId();
        UserAvailableShifts us = availabilityDB.getAvailabilityForUser(id);
      //  Mail.sendMailConfirm(); //Mailkassen blir ikke initialisert..
        return us;
    }

    //Gets a list of shifts to set available for this user
    @Path("/setAvailable")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String save(String list) {
        if(getSession() == null) return null;
        JSONArray arr = new JSONArray(list);
        List<Integer> list1 = new ArrayList<Integer>();
        for(int i = 0; i < arr.length(); i++){
            list1.add(arr.getJSONObject(i).getInt("id"));
            int num = arr.getJSONObject(i).getInt("id");
            int id = getSession().getUser().getId();

            boolean ok = availabilityDB.setAvailability(id, num);
        }
        return "correct";
    }

    //Metode som kjører hver gang noen setter seg tilgjengelig
    //Og hver gang noen setter som syk eller bytte shift

    /*TODO delete if not used
    //Gets available shifts for specific date
    @Deprecated
    @GET
    @Path("/{dateString}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftAvailable> getShiftsAvailable(@PathParam("dateString") String dateString) {
        ArrayList<ShiftAvailable> out = availabilityDB.getAvailabilityForDate(dateString);
        return out;
    }
    */

    @GET
    @Path("/date")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftAvailable> getShifts(@QueryParam("daysForward") int daysForward,
                                                      @QueryParam("date") Date date){
        if (getSession() == null) return null;
        if(date == null) date = new Date(System.currentTimeMillis());
        return availabilityDB.getShiftsForDate(daysForward, getSession().getUser().getId(), date);
    }

    @Path("/deleteAvailable")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String delAvailability(String list) {
        if (getSession() == null) return null;
        JSONArray arr = new JSONArray(list);
        List<Integer> list1 = new ArrayList<Integer>();
        for(int i = 0; i < arr.length(); i++){
            list1.add(arr.getJSONObject(i).getInt("id"));
            int num = arr.getJSONObject(i).getInt("id");
            int id = getSession().getUser().getId();

            boolean ok = availabilityDB.deleteAvailability(id, num);
        }
        return "correct";
    }


    @GET
    @Path("/shift/{shiftId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailableUsersForShift(@PathParam("shiftId") int shiftId, @QueryParam("category") String categoryString, @QueryParam("limitByCategory") boolean onlyThisCategory){
        if(!getSession().isAdmin()) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        AvailableUsersUtil availUsersU = new AvailableUsersUtil();
        ShiftDBManager shiftDBM = new ShiftDBManager();
        Shift shift = shiftDBM.getShift(shiftId);
        User.UserCategory category = User.UserCategory.valueOf(categoryString);
        LocalDate date = shift.getDate().toLocalDate();
        ArrayList<UserBasicWorkHours> userList = availUsersU.sortAvailableEmployeesWithCategory(shiftId, date, category, onlyThisCategory);
        GenericEntity entity = new GenericEntity<List<UserBasicWorkHours>>(userList){};

        return Response.ok(entity).build();
    }

    // Only used in tests, disabled annotations
    //@DELETE
    //@Path("/{shiftId}")
    //@Consumes(MediaType.APPLICATION_JSON)
    public Response deleteAvailability(/*@QueryParam("brukerId")*/ int userId, /*@PathParam("shiftId")*/ int shiftId) {
        boolean isDeleted = availabilityDB.deleteAvailability(userId, shiftId);
        if (!isDeleted) {
            return Response.status(400).entity("Unable to delete availability.").build();
        } else {
            return Response.status(200).build();
        }
    }
}
