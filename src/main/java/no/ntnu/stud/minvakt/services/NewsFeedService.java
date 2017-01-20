package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.ArrayList;

/**
 * Created by evend on 1/20/2017.
 */
@Path("newsfeed")
public class NewsFeedService extends SecureService{
    NewsFeedDBManager newsDB = new NewsFeedDBManager();
    public NewsFeedService(@Context HttpServletRequest request) {
        super(request);
    }

    @GET
    public ArrayList<NewsFeedItem> getNewsFeed(){
        if(getSession().isAdmin()){
            return newsDB.getNewsFeedAdmin();
        }
        else{
            return newsDB.getNewsFeed(getSession().getUser().getId());
        }
    }


}
