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
 * Created by evend on 1/20/2017.
 */
@Path("/newsfeed")
public class NewsFeedService extends SecureService{
    NewsFeedDBManager newsDB = new NewsFeedDBManager();
    public NewsFeedService(@Context HttpServletRequest request) {
        super(request);
    }

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

    @POST
    @Path("/{feedId}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response setResolved(@PathParam("feedId") int feedId, @DefaultValue("true") @FormParam("accepted") boolean accepted){

        boolean isUpdated = ShiftChangeUtil.updateNotification(feedId, accepted);
        if(isUpdated){
            return Response.status(Response.Status.OK).build();
        }
        else{
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }
}
