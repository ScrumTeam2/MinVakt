package no.ntnu.stud.minvakt.services;


import no.ntnu.stud.minvakt.data.*;
import no.ntnu.stud.minvakt.data.shift.ShiftUserAvailability;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.UserAvailableShifts;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;
import org.json.JSONArray;
import org.json.JSONObject;
import no.ntnu.stud.minvakt.data.shift.ShiftAvailable;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Path("/availability")
public class AvailabilityService extends SecureService {
    AvailabilityDBManager availabilityDB = new AvailabilityDBManager();

    public AvailabilityService(@Context HttpServletRequest request) {
        super(request);
    }

    @POST
    public Response setAvailability(@QueryParam("userId") int userId, @QueryParam("shiftId") int shiftId) {

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
        System.out.println("Get availability");
        int id = getSession().getUser().getId();
        UserAvailableShifts us = availabilityDB.getAvailabilityForUser(id);
        System.out.println(us.getShifts());
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

            System.out.println(num);
            boolean ok = availabilityDB.setAvailability(id, num);
            System.out.println(ok);
        }
        return "correct";
    }

    //Metode som kjører hver gang noen setter seg tilgjengelig
    //Og hver gang noen setter som syk eller bytte shift

    //Gets available shifts for specific date
    @Deprecated
    @GET
    @Path("/{dateString}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftAvailable> getShiftsAvailable(@PathParam("dateString") String dateString) {
        ArrayList<ShiftAvailable> out = availabilityDB.getAvailabilityForDate(dateString);
        return out;
    }

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

            System.out.println(num);
            boolean ok = availabilityDB.deleteAvailability(id, num);
            System.out.println(ok);
        }
        return "correct";
    }

    @DELETE
    @Path("/{shiftId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteAvailability(@QueryParam("brukerId") int brukerId, @PathParam("shiftId") int shiftId) {
        if (getSession() == null) return null;
        boolean isDeleted = availabilityDB.deleteAvailability(brukerId, shiftId);
        if (!isDeleted) {
            return Response.status(400).entity("Unable to delete availability.").build();
        } else {
            return Response.status(200).build();
        }
    }
}
