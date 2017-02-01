package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.constraints.AssertTrue;
import java.sql.Timestamp;
import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by evend on 1/20/2017.
 */
public class NewsFeedDBManagerTest {
    private static NewsFeedDBManager newsFeedDB;

    @BeforeClass
    public static void DBsetUp() {
        newsFeedDB = new NewsFeedDBManager();
    }

    public int createTestData() {
        Timestamp date = Timestamp.valueOf("1995-01-01 00:00:00");
        NewsFeedItem notification = new NewsFeedItem(-1, date, "Test", 26, 1, 1,
                NewsFeedItem.NewsFeedCategory.SHIFT_CHANGE_ADMIN);
        return newsFeedDB.createNotification(notification);
    }

    public void deleteTestData(int id) {
        newsFeedDB.deleteNotification(id);
    }

    @Test
    public void createAndDeleteNotification() {
        Timestamp date = Timestamp.valueOf("1995-01-01 00:00:00");
        NewsFeedItem notification = new NewsFeedItem(-1, date, "Test", 1, 1, 1,
                NewsFeedItem.NewsFeedCategory.SHIFT_CHANGE_ADMIN);
        int id = newsFeedDB.createNotification(notification);
        assertTrue(id != 0);
        boolean isDeleted = newsFeedDB.deleteNotification(id);
        assertTrue(isDeleted);
        isDeleted = newsFeedDB.deleteNotification(id);
        assertFalse(isDeleted);
    }

    @Test
    public void getNewsFeed() {
        int id = createTestData();
        ArrayList<NewsFeedItem> items = newsFeedDB.getNewsFeed(26);
        assertFalse(items.isEmpty());
        deleteTestData(id);

    }
    @Test
    public void getNewsFeedAdmin() {
        int id = createTestData();
        ArrayList<NewsFeedItem> items = newsFeedDB.getNewsFeedAdmin();
        assertFalse(items.isEmpty());
        deleteTestData(id);
    }

    @Test
    public void getShiftChangeCountPending() throws Exception {
        int countUnRes = newsFeedDB.getShiftChangeCountPending(22, 1);
        Assert.assertEquals(0, countUnRes);
    }

    @Test
    public void userHasFeed() throws Exception {
        final int userId = 1;
        final int validFeedId = 1;
        final int invalidFeedId = 0;

        Assert.assertTrue(newsFeedDB.userHasFeed(userId, validFeedId));
        Assert.assertFalse(newsFeedDB.userHasFeed(userId, invalidFeedId));
    }

}
