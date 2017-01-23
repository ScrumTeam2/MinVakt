package no.ntnu.stud.minvakt.services;


import no.ntnu.stud.minvakt.data.ShiftAvailable;
import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.data.UserAvailableShifts;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
        return us;
    }

    //Gets a list of shifts to set available for this user
    @Path("/setAvailable")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String save(String list) {
        if(getSession() == null) {
            return null;
        }
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
    //public Response checkLogin(@Context HttpServletRequest request, @FormParam("identificator") int[] identificator, @FormParam("password") String password) {

    /*
        Klient:
        1) Når dato er trykket på, få shiftID's for valgt dato
        2) Klient interpret time og shiftId
        3)
        Grønn dato i kalender -> Shift i denne datoen trenger folk(har mangel)
        REST:
        1) Når dato er trykket på, få dato og send tilbake shifts for dato

        1) Legge til tilgjengelihghet   -> Få liste av shiftIDS fra klient, og valget om de er satt til tilgjengelig eller ikke
        2) Endre/Slette tilgjengelighet -> Hente tilgjengelighet
        3)
    */
    //Metode som kjører hver gang noen setter seg tilgjengelig
    //Og hver gang noen setter som syk eller bytte shift

    /*
    @Path("/date")
    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getShiftForRequestedDate(@Context HttpServletRequest request, @FormParam("dateSelection") String dateString ) {
        // Return if already logged in
        if (request.getSession() == null) return Response.status(Response.Status.UNAUTHORIZED).build();
        //ArrayList<ShiftAvailable> out = availabilityDB.getAvailabilityForDate(dateString);
        //return out;
    }
    */
    /*
    @GET
    @Path("/{shiftId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAvailability(@PathParam("shiftId") int shiftId){
        ArrayList<Integer> out = availabilityDB.getAvailability(shiftId);
        if(out.isEmpty()){
            return Response.status(400).entity("Unable to find available employees.").build();
        }else {
            String json = "{\"id\": \"" + out + "\"}";
            //return Response.status(200).entity(json).build();
            return Response.ok(json, MediaType.APPLICATION_JSON).build();

        }
    }
    */
    //Gets available shifts for specific date
    @GET
    @Path("/{dateString}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftAvailable> getShiftsAvailable(@PathParam("dateString") String dateString) {
        ArrayList<ShiftAvailable> out = availabilityDB.getAvailabilityForDate(dateString);
        return out;
    }

    @Path("/deleteAvailable")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public String delAvailability(String list) {
        if(getSession() == null) {
            return null;
        }
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
        boolean isDeleted = availabilityDB.deleteAvailability(brukerId, shiftId);
        if (!isDeleted) {
            return Response.status(400).entity("Unable to delete availability.").build();
        } else {
            return Response.status(200).build();
        }
    }
}
