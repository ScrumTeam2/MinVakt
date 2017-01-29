package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.util.ContentUtil;
import no.ntnu.stud.minvakt.data.shift.*;
import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shift.ShiftUser;
import no.ntnu.stud.minvakt.data.shift.ShiftUserAvailability;
import no.ntnu.stud.minvakt.data.shift.ShiftUserBasic;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;
import no.ntnu.stud.minvakt.util.AvailableUsersUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;


/**
 * Class for creating shifts
 * Parameter date needs to be on correct format
 */
@Path("/shift")
public class ShiftService extends SecureService{
    ShiftDBManager shiftDB = new ShiftDBManager();
    UserDBManager userDB = new UserDBManager();
    NewsFeedDBManager newsDB = new NewsFeedDBManager();
    ContentUtil contentUtil = new ContentUtil();

    public ShiftService(@Context HttpServletRequest request) {
        super(request);
    }

    /**
     * @param shift Shift object to be
     * @return
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createShift(Shift shift) {
        if (!getSession().isAdmin()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot access service").build();
        }

        int shiftId = shiftDB.createNewShift(shift);
        shift.setId(shiftId);
        if (shiftId < 0) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Unable to create new shift.").build();
        } else {
            String json = "{\"id\": \"" + shiftId + "\"}";
            return Response.ok(json, MediaType.APPLICATION_JSON).build();
        }

    }
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserAvailability> getShifts(@QueryParam("daysForward") int daysForward,
                                                      @QueryParam("date") Date date, @DefaultValue("-1")@QueryParam("deptId") int deptId){
        if(getSession() == null) return null;
        if(deptId <= 0) deptId = getSession().getUser().getDeptId();
        if(date == null) date = new Date(System.currentTimeMillis());

        return shiftDB.getShifts(daysForward, getSession().getUser().getId(), date, deptId);
    }
    @GET
    @Path("/shiftsAvailability")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserAvailability> getShiftsDisregardDept(@QueryParam("daysForward") int daysForward,
                                                      @QueryParam("date") Date date){
      //  if(deptId <= 0) deptId = getSession().getUser().getDeptId();
        if(getSession() == null) return null;
        if(date == null) date = new Date(System.currentTimeMillis());
        return shiftDB.getShiftsNoDept(daysForward, getSession().getUser().getId(), date);
    }

    @DELETE
    @Path("/{shiftId}")
    public Response deleteShift(@PathParam("shiftId") int id) {
        if (!getSession().isAdmin()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot access service").build();
        }

        boolean isDeleted = shiftDB.deleteShift(id);
        if (!isDeleted) {
            return Response.status(400).entity("Unable to delete shift.").build();
        } else {
            return Response.status(200).build();
        }
    }

    @GET
    @Path("/{shiftId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Shift getShift(@PathParam("shiftId") int shiftId) {
        if (getSession() == null) return null;

        return shiftDB.getShift(shiftId);
    }

    /*
        Parameters:

     */
    @POST
    @Path("/{shiftId}/user/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addEmployeeToShift(@PathParam("userId") int userId, @PathParam("shiftId") int shiftId) {
        if (!getSession().isAdmin()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot access service").build();
        }

        User userData = userDB.getUserById(userId);
        ShiftUser shiftUser = new ShiftUser(userId, userData.getFirstName() + " " + userData.getLastName(), userData.getCategory(), false, 0, userData.getDeptId());

        boolean statusOk = shiftDB.addEmployeeToShift(shiftUser, shiftId);
        if (statusOk) {
            Response res = Response.status(200).build();
            return res;
        } else {
            return Response.status(400).entity("Unable to add employee").build();
        }
    }

    @DELETE
    @Path("/{shiftId}/user/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response deleteEmployeeFromShift(@PathParam("userId") int userId, @PathParam("shiftId") int shiftId,
                                            @QueryParam("findNewEmployee") boolean findNewEmployee) {
        if (!getSession().isAdmin()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot access service").build();
        }

        boolean statusOk = false;
        if(!findNewEmployee) {
            statusOk = shiftDB.deleteEmployeeFromShift(userId, shiftId);

        }
        else {
          //  statusOk = ShiftChangeUtil.findNewUserToShift(shiftId, userId);
        }
        if (statusOk) {
            return Response.status(200).build();
        } else {
            return Response.status(400).entity("Unable to delete employee").build();
        }
    }

    @POST
    @Path("/{shiftId}/replaceuser")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response replaceEmployeeOnShift(@PathParam("shiftId") int shiftId, @FormParam("oldUserId") int oldUserId, @FormParam("newUserId") int newUserId) {
        if (!getSession().isAdmin()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot access service").build();
        }

        boolean statusOk = shiftDB.replaceEmployeeOnShift(shiftId, oldUserId, newUserId);
        if (statusOk) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Unable to add employee").build();
        }
    }

    @POST
    @Path("/{shiftId}/set_staff")
    public Response setStaffCount(@PathParam("shiftId") int shiftId, @FormParam("staffCount") int newStaffCount) {
        if (!getSession().isAdmin()) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Cannot access service").build();
        }

        // Do not accept staff count below some number
        if(newStaffCount < 1) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if(shiftDB.setStaffNumberOnShift(shiftId, newStaffCount)) {
            return Response.ok().build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @GET
    @Path("/user/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserBasic> getUserBasicFromSession(@QueryParam("date") Date date) {
        if(date == null)date = new Date(System.currentTimeMillis());
        return shiftDB.getShiftWithUserId(getSession().getUser().getId(), date);
    }

//    /**
//     * Generates a list of possible candidates for a specific shift
//     * @param shiftId
//     * @return A Response containing an array of Users
//     */
//    @GET
//    @Path("/{shiftId}/possiblecandidates")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getPossibleCandidates(@PathParam("shiftId") int shiftId) {
//        if (getSession() == null || !getSession().isAdmin()) {
//            return Response.status(Response.Status.UNAUTHORIZED).build();
//        }
//
//        Shift shift = new ShiftDBManager().getShift(shiftId);
//        if (shift == null) {
//            return Response.status(Response.Status.BAD_REQUEST).build();
//        }
//
//        ShiftPlanController controller = new ShiftPlanController(shift);
//        ArrayList<UserBasicWorkHours> candidates = controller.getPossibleCandidates();
//
//        // Add the candidates to db (admin can change these)
//        controller.savePossibleCandidates();
//
//        GenericEntity<List<UserBasicWorkHours>> entity =
//                new GenericEntity<List<UserBasicWorkHours>>(Lists.newArrayList(candidates)) {
//                };
//
//
//        return Response.ok().entity(entity).build();
//    }

    @GET
    @Path("user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserBasic> getUserBasicFromId(@PathParam("userId") int userId){
        if (!getSession().isAdmin()) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }
        return shiftDB.getShiftWithUserId(userId, new Date(System.currentTimeMillis()));
    }

    @GET
    @Path("/availableShifts")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftAvailable> getAvailableShifts(){
        if(getSession() == null) return null;

        return shiftDB.getAvailableShifts();
    }

    //Registrates absence
    @GET
    @Path("/user/valid_absence/{shiftId}")
    public Response requestValidAbsence(@PathParam("shiftId") int shiftId){
        Timestamp timestamp = Timestamp.from(Instant.now());
        User user = getSession().getUser();
        Shift shift = shiftDB.getShift(shiftId);

        // Check if shift starts within 2 hours
        if(LocalDateTime.now().until(shift.getStartTime(), ChronoUnit.HOURS) < 2) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Fristen for å melde sykdom har gått ut.").build();
        }
      
        // Check if the user actually is on this shift
        if(!shift.getShiftUsers().stream().anyMatch(u -> u.getUserId() == user.getId())) {
            throw new BadRequestException("Invalid shift ID");
        }

        //Set valid_absence = 1. valid_absence = 2 når admin godkjenner.
        boolean ok = shiftDB.setValidAbsence(user.getId(), shiftId, 1);
        int adminId = userDB.getAdminId();
        NewsFeedItem notification = new NewsFeedItem(-1, timestamp,
                contentUtil.validAbsence(user), adminId, user.getId(), shiftId,
                NewsFeedItem.NewsFeedCategory.VALID_ABSENCE);
        if(newsDB.createNotification(notification) != 0){
            return Response.ok().entity("Notification sent to administration.").build();
        }
        else{
            return Response.notModified().entity("Notification not created.").build();
        }
    }

    @GET
    @Path("/user/shift_change/{shiftId}")
    public Response requestShiftChange(@PathParam("shiftId") int shiftId){
        Timestamp timestamp = Timestamp.from(Instant.now());
        User user = getSession().getUser();
        Shift shift = shiftDB.getShift(shiftId);

        //tries to set shift change
        if(shiftDB.setShiftChange(shiftId, user.getId())){

            //sends notifications to users/administrator, depending on the situation
            AvailableUsersUtil availableUsers = new AvailableUsersUtil();
            boolean ok = availableUsers.sendNotificationOfShiftChange(shift, user, timestamp);

            if(ok){
                return Response.ok().entity("Notification(s) sent.").build();
            }
            else{
                return Response.notModified().entity("Notification not created.").build();
            }
        }else{
            return Response.status(400).entity("Could not set shift change.").build();
        }
    }
}
