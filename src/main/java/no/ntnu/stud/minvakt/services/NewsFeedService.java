package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;
import no.ntnu.stud.minvakt.util.ShiftChangeUtil;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

/**
 * Contains REST endpoints for both the user and admin news feed
 */
@Path("/newsfeed")
public class NewsFeedService extends SecureService{
    NewsFeedDBManager newsDB = new NewsFeedDBManager();
    public NewsFeedService(@Context HttpServletRequest request) {
        super(request);
    }

    /**fetches all news feed notifications from database
     * @return ArrayList with news feed notifications as NewsFeedItem objects
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ArrayList<NewsFeedItem> getNewsFeed(){
        if(getSession().isAdmin()){
            return newsDB.getNewsFeedAdmin();
        }
        else{
            return newsDB.getNewsFeed(getSession().getUser().getId());
        }
    }

    /** updates news feed notification given by ID, according to which news feed category the news feed item is.
     * @param feedId -  the id of the news feed notification
     * @param accepted - boolean used by cases where something is to be accepted or declined, default true if not applicable
     * @return  response OK if successful
     *          response BAD_REQUEST if not successful
     */
    @POST
    @Path("/{feedId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setResolved(@PathParam("feedId") int feedId, @DefaultValue("true") @FormParam("accepted") boolean accepted){
        if(!getSession().isAdmin() && !newsDB.userHasFeed(getSession().getUser().getId(), feedId)) {
            throw new BadRequestException("Invalid feed ID");
        }

        boolean isUpdated = ShiftChangeUtil.updateNotification(feedId, accepted);
        if(isUpdated){
            return Response.status(Response.Status.OK).build();
        }
        else{
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
