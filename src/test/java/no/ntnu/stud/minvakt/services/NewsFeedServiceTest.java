package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;
import org.junit.*;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Marit on 26.01.2017.
 */
public class NewsFeedServiceTest extends ServiceTest{
    private NewsFeedService newsFS;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        newsFS = new NewsFeedService(request);
    }

    @Test
    public void getNewsFeedAdmin() throws Exception {
        logInAdmin();
        ArrayList<NewsFeedItem> nfi = newsFS.getNewsFeed();
        int expRes = 3;
        int actual = nfi.get(0).getFeedId();
        Assert.assertEquals(expRes, actual);
    }

    @Test
    public void getNewsFeedUser() throws Exception {
        logInUser();
        ArrayList<NewsFeedItem> nfi = newsFS.getNewsFeed();
        int expRes = 1;
        int actual = nfi.get(0).getFeedId();
        Assert.assertEquals(expRes, actual);
    }

    @Test
    public void setResolved() throws Exception {
        logInUser();
        Timestamp date = Timestamp.valueOf("1995-01-01 00:00:00");
        NewsFeedItem notification = new NewsFeedItem(-1, date, "Test", 1, 1, 1,
                NewsFeedItem.NewsFeedCategory.NOTIFICATION);
        NewsFeedDBManager newsFeedDBM = new NewsFeedDBManager();
        int feedId = newsFeedDBM.createNotification(notification);
        Response response = newsFS.setResolved(feedId, true);
        Assert.assertEquals(200, response.getStatus());
        newsFeedDBM.deleteNotification(feedId);
    }
}
