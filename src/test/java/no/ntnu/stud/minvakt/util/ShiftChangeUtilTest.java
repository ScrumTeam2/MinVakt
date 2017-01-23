package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import no.ntnu.stud.minvakt.data.Overtime;
import no.ntnu.stud.minvakt.database.NewsFeedDBManager;
import no.ntnu.stud.minvakt.database.OvertimeDBManager;
import no.ntnu.stud.minvakt.database.UserDBManager;
import no.ntnu.stud.minvakt.services.ShiftService;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.sql.Timestamp;

import static org.junit.Assert.assertTrue;

/**
 * Created by evend on 1/22/2017.
 */

public class ShiftChangeUtilTest{
    private static NewsFeedDBManager newsFeedDB;
    private static OvertimeDBManager overtimeDB;

    @BeforeClass
    public static void DBsetUp() {
        newsFeedDB = new NewsFeedDBManager();
        overtimeDB = new OvertimeDBManager();
    }

    @Test
    public void updateNotification(){
        Timestamp date = Timestamp.valueOf("1995-01-01 00:00:00");

        NewsFeedItem notification = new NewsFeedItem(-1, date,
                "Test", 1,1,4, NewsFeedItem.NewsFeedCategory.NOTIFICATION);
        int feedId = newsFeedDB.createNotification(notification);
        assertTrue(ShiftChangeUtil.updateNotification(feedId, true));
        assertTrue(ShiftChangeUtil.updateNotification(feedId, false));
        newsFeedDB.deleteNotification(feedId);
    }
    @Test
    public void acceptShiftChangeAdmin(){
        Timestamp date = Timestamp.valueOf("1995-01-01 00:00:00");
        NewsFeedItem notification = new NewsFeedItem(-1, date,
                "Test", 1,1,4, NewsFeedItem.NewsFeedCategory.SHIFT_CHANGE_ADMIN);
        int feedId = newsFeedDB.createNotification(notification);
        assertTrue(ShiftChangeUtil.updateNotification(feedId, true));
        newsFeedDB.deleteNotification(feedId);
    }
    @Test
    public void acceptShiftChangeEmployee(){
        Timestamp date = Timestamp.valueOf("1995-01-01 00:00:00");

        NewsFeedItem notification = new NewsFeedItem(-1, date,
                "Test", 1,1,4, NewsFeedItem.NewsFeedCategory.SHIFT_CHANGE_EMPLOYEE);
        int feedId = newsFeedDB.createNotification(notification);
        assertTrue(ShiftChangeUtil.updateNotification(feedId, true));
        newsFeedDB.deleteNotification(feedId);
    }
    @Ignore
    public void updateTimeBank(){
        Timestamp date = Timestamp.valueOf("1995-01-01 00:00:00");

        NewsFeedItem notification = new NewsFeedItem(-1, date,
                "Test", 1,1,4, NewsFeedItem.NewsFeedCategory.TIMEBANK);
        overtimeDB.setOvertime(1,1,0,60);
        int feedId = newsFeedDB.createNotification(notification);
        assertTrue(ShiftChangeUtil.updateNotification(feedId, true));
    }
}
