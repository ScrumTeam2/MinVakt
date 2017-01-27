package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Content;
import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.data.Overtime;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;
import no.ntnu.stud.minvakt.database.OvertimeDBManager;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marit on 20.01.2017.
 */

@Path("/overtime")
public class OvertimeService extends SecureService{
    OvertimeDBManager overtimeDBM = new OvertimeDBManager();
    NewsFeedDBManager newsfeedDBM = new NewsFeedDBManager();
    UserDBManager userDBM = new UserDBManager();
    Content content = new Content();

    public OvertimeService(@Context HttpServletRequest request) {
        super(request);
    }

    //POST register overtime (used by employee-user)
    //setOvertime(int userId, int shiftId, int startTime, int minutes) returnes true/false
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setOvertime(Overtime overtime) {
        User user;

        if(getSession().isAdmin()){
            user = userDBM.getUserById(overtime.getUserId());
        }else{
            user = getSession().getUser();
        }

        boolean isRegistered = overtimeDBM.setOvertime(user.getId(), overtime.getShiftId(), overtime.getStartTime(), overtime.getMinutes());

        if(isRegistered) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            int adminId = userDBM.getAdminId();

            if(adminId==0){
                return Response.status(400).entity("Overtime registered, but could not find admin user").build();
            }else {
                NewsFeedItem notification = new NewsFeedItem(-1, timestamp,
                        content.regTimebank(user), adminId, user.getId(), overtime.getShiftId(), NewsFeedItem.NewsFeedCategory.TIMEBANK, overtime.getStartTime());
                int newsfeedId = newsfeedDBM.createNotification(notification);

                if (newsfeedId == 0){
                    return Response.status(400).entity("Overtime registered, but sending notification to admin failed").build();
                }else{
                    return Response.status(200).build();
                }
            }
        }else{
            return Response.status(400).entity("Could not register overtime").build();
        }
    }

    //GET fetch overtime for user (used by employee-user)
    //getOvertimeListByUserId(int userId) returns Overtime[]
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOvertimeByUserId() {
        int userId = getSession().getUser().getId();

        ArrayList<Overtime> overtime = overtimeDBM.getOvertimeListByUserId(userId);
        GenericEntity entity = new GenericEntity<List<Overtime>>(overtime) {};
        return Response.ok(entity).build();
    }

    @GET
    @Path("/shiftId/{shiftId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getOvertimeByShiftId(@PathParam("shiftId") int shiftId, @QueryParam("userId") int userId) {
        //System.out.println("userid: "+userId+", shiftId: "+shiftId);
        ArrayList<Overtime> overtime = overtimeDBM.getOvertimeByShift(userId, shiftId);
        GenericEntity entity = new GenericEntity<List<Overtime>>(overtime) {};
        return Response.ok(entity).build();
    }


    @DELETE
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteOvertime(@PathParam("userId") int userId, @QueryParam("shiftId") int shiftId, @QueryParam("startTime") int startTime) {

        int feedId = newsfeedDBM.getNewsFeedIdThroughOvertime(userId, shiftId, startTime);
        if(feedId<1){
            return Response.status(400).entity("Could not find feedId").build();
        }else{
            boolean delNotification = newsfeedDBM.deleteNotification(feedId);

            if (delNotification){
                boolean isDeleted = overtimeDBM.deleteOvertime(userId, shiftId, startTime);

                if(isDeleted){
                    return Response.status(200).build();
                }else{
                    return Response.status(400).entity("Could not delete overtime").build();
                }

            }else{
                return Response.status(400).entity("Could not delete overtime notification in newsfeed. Overtime ot deleted.").build();
            }

        }

    }
}
