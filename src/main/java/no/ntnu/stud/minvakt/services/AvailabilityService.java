package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.UserAvailableShifts;
import no.ntnu.stud.minvakt.data.user.UserBasicWorkHours;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.util.AvailableUsersUtil;
import org.json.JSONArray;
import no.ntnu.stud.minvakt.data.shift.ShiftAvailable;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
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

    /**
     * Gets object with shifts for given user where user is available
     * @return UserAvailableShifts object
     */
    @GET
    @Path("/user")
    @Produces(MediaType.APPLICATION_JSON)
    public UserAvailableShifts getAvailabilityUser() {
        if (getSession() == null) return null;
        int id = getSession().getUser().getId();
        UserAvailableShifts us = availabilityDB.getAvailabilityForUser(id);
        return us;
    }

    /**
     * Gets a list of shifts to set available for given user
     * @return string
     */
    //Gets a list of shifts to set available for this user
    @Path("/setAvailable")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String setAvailability(String list) {
        if(getSession() == null) return null;
        JSONArray arr = new JSONArray(list);
        List<Integer> list1 = new ArrayList<Integer>();
        for(int i = 0; i < arr.length(); i++){
            list1.add(arr.getJSONObject(i).getInt("id"));
            int num = arr.getJSONObject(i).getInt("id");
            int id = getSession().getUser().getId();

            availabilityDB.setAvailability(id, num);
        }
        return "correct";
    }


    /**
     * Gets shifts from a date a given number of days forward
     * @param daysForward Number of days
     * @param date The date
     * @return ArrayList<ShiftAvailable>
     */
    @GET
    @Path("/date")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftAvailable> getShifts(@QueryParam("daysForward") int daysForward, @QueryParam("date") Date date){
        if (getSession() == null) return null;
        if(date == null) date = new Date(System.currentTimeMillis());
        return availabilityDB.getShiftsForDate(daysForward, getSession().getUser().getId(), date);
    }

    /**
     * Deletes availability from user
     * @param list List with shiftIDs
     * @return string
     */
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

            availabilityDB.deleteAvailability(id, num);
        }
        return "correct";
    }


    /**
     * Gets a list with available users for a shift
     * @param shiftId ID for shift
     * @param categoryString Category ("ASSISTANT", "HEALTH_WORKER", "NURSE")
     * @param onlyThisCategory If true: Only get employees from specific category
     * @return If success: Response OK
     *         If not admin: Response UNAUTHORIZED
     */
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
        //Sends arrayList as GenericEntity
        ArrayList<UserBasicWorkHours> userList = availUsersU.sortAvailableEmployeesWithCategory(shiftId, date, category, onlyThisCategory);
        GenericEntity entity = new GenericEntity<List<UserBasicWorkHours>>(userList){};

        return Response.ok(entity).build();
    }
}
