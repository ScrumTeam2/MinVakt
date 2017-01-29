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

    /** Creates new shift in database
     * @param shift Shift object of shift to be created
     * @return  if not admin: Response UNAUTHORIZED
     *          if not successful: Response BAD_REQUEST
     *          if successful: Response OK
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

    //TODO: hva er egentlg ShiftUserAvailability?
    /** Fetches shifts for an employee using the session userId
     * @param daysForward how many days forward to get shifts
     * @param date the date to start from when fetching shifts
     * @param deptId the ID of the department (default set to -1)
     * @return Arraylist of ShiftUserAvailability objects
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserAvailability> getShifts(@QueryParam("daysForward") int daysForward,
                                                      @QueryParam("date") Date date, @DefaultValue("-1")@QueryParam("deptId") int deptId){
        if(getSession() == null) return null;
        if(deptId <= 0) deptId = getSession().getUser().getDeptId();
        if(date == null) date = new Date(System.currentTimeMillis());

        return shiftDB.getShifts(daysForward, getSession().getUser().getId(), date, deptId);
    }

    //TODO: skriv javadoc
    /**
     * @param daysForward
     * @param date
     * @return
     */
    @GET
    @Path("/shiftsAvailability")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserAvailability> getShiftsDisregardDept(
            @QueryParam("daysForward") int daysForward, @QueryParam("date") Date date){

        if(getSession() == null) return null;
        if(date == null) date = new Date(System.currentTimeMillis());
        return shiftDB.getShiftsNoDept(daysForward, getSession().getUser().getId(), date);
    }

    /** deletes shift from database
     * @param id - the ID of the shift
     * @return  if not admin: Response UNAUTHORIZED
     *          if not successful: Response BAD_REQUEST
     *          if successful: Response OK
     */
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

    /**Get a shift from database with shiftId
     * @param shiftId - ID of shift to get
     * @return Shift object
     */
    @GET
    @Path("/{shiftId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Shift getShift(@PathParam("shiftId") int shiftId) {
        if (getSession() == null) return null;

        return shiftDB.getShift(shiftId);
    }


    /** Add employee to shift
     * @param userId - the ID of the employee to be added
     * @param shiftId - the ID of the shift
     * @return  if not admin: Response UNAUTHORIZED
     *          if not successful: Response BAD_REQUEST
     *          if successful: Response OK
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

    /** remove an employee from a shift
     * @param userId - the ID of the employee to be removed
     * @param shiftId - the ID of the shift
     * @param findNewEmployee - boolean whether to find a new employee for this shift or not
     * @return  if not admin: Response UNAUTHORIZED
     *          if not successful: Response BAD_REQUEST
     *          if successful: Response OK
     */
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

    /** replaces one employee with another
     * @param shiftId - the ID of the shift
     * @param oldUserId - the ID of the employee who previously worked on this shift
     * @param newUserId - the ID of the new employee
     * @return  if not admin: Response UNAUTHORIZED
     *          if not successful: Response BAD_REQUEST
     *          if successful: Response OK
     */
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

    /**Set the number of employees that this shift should have
     * @param shiftId - the ID of the shift
     * @param newStaffCount - the new staffcount for this shift (number of employees who should work)
     * @return  if not admin: Response UNAUTHORIZED
     *          if not successful: Response BAD_REQUEST
     *          if successful: Response OK
     */
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

    /**Fetches the shifts worked on a single day for the user identified with the Session userID
     * @param date - the date of the shift
     * @return ArrayList with ShiftUserBasic objects
     */
    @GET
    @Path("/user/")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserBasic> getUserBasicFromSession(@QueryParam("date") Date date) {
        if(date == null)date = new Date(System.currentTimeMillis());
        return shiftDB.getShiftWithUserId(getSession().getUser().getId(), date);
    }

    /** Fetches an arraylist of all the shifts for a single employee as ShiftUserBasic objects
     * @param userId - the ID of the employee that should be fetched
     * @return  if not admin: Response UNAUTHORIZED
     *          if successful: ArrayList with ShiftUserBasic objects
     */
    @GET
    @Path("user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftUserBasic> getUserBasicFromId(@PathParam("userId") int userId){
        if (!getSession().isAdmin()) {
            throw new NotAuthorizedException("Cannot access service", Response.Status.UNAUTHORIZED);
        }
        return shiftDB.getShiftWithUserId(userId, new Date(System.currentTimeMillis()));
    }

    /**Fetches the shifts that does not have the required number of employees
     * @return ArrayList with ShiftsAvailable objects
     */
    @GET
    @Path("/availableShifts")
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<ShiftAvailable> getAvailableShifts(){
        if(getSession() == null) return null;

        return shiftDB.getAvailableShifts();
    }

    /** registers a valid absence request and notifies admin
     * @param shiftId - the ID of the shift
     * @return  if it's too late to register absence: Response BAD_REQUEST
     *          if not successful: Response NOT_MODIFIED
     *          if successful: Response OK
     */
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

    /** uses the session userID to request a shift change for the employee, sends notification(s)
     * @param shiftId - the ID of the shift
     * @return  if notification not created: Response NOT_MODIFIED
     *          if not successful: Response BAD_REQUEST
     *          if successful: Response OK
     */
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
