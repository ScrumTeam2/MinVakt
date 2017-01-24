package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.data.Overtime;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;
import no.ntnu.stud.minvakt.database.OvertimeDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;

/**
 * Created by Marit on 20.01.2017.
 */

@Path("/overtime")
public class OvertimeService extends SecureService{
    OvertimeDBManager overtimeDBM = new OvertimeDBManager();
    NewsFeedDBManager newsfeedDBM = new NewsFeedDBManager();
    UserDBManager userDBM = new UserDBManager();

    public OvertimeService(@Context HttpServletRequest request) {
        super(request);
    }

    //POST register overtime (used by employee-user)
    //setOvertime(int userId, int shiftId, int startTime, int minutes) returnes true/false
    @POST
    @Path("/{userId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setOvertime(Overtime overtime) {
        boolean isRegistered = overtimeDBM.setOvertime(overtime.getUserId(), overtime.getShiftId(), overtime.getStartTime(), overtime.getMinutes());

        if(isRegistered) {
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String content = "Tekst";
            int adminId = userDBM.getAdminId();

            if(adminId==0){
                return Response.status(400).entity("Overtime registered, but could not find admin user").build();
            }else {
                NewsFeedItem notification = new NewsFeedItem(-1, timestamp, content, adminId, overtime.getUserId(), overtime.getShiftId(), NewsFeedItem.NewsFeedCategory.TIMEBANK, overtime.getStartTime());
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
    //getOvertimeByUserId(int userId) returns Overtime[]
    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Overtime[] getOvertime(@PathParam("userId") int userId) {
        Overtime[] overtime = overtimeDBM.getOvertimeByUserId(userId);
        return overtime;
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
