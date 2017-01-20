package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.NewsFeedItem;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;

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

        @Test
        public void createAndDeleteNotification(){
            Timestamp date = Timestamp.valueOf("1995-01-01 00:00:00");
            NewsFeedItem notification = new NewsFeedItem(-1,date,"Test",1,1,1);
            int id = newsFeedDB.createNotification(notification);
            assertTrue(id != 0);
            boolean isDeleted = newsFeedDB.deleteNotification(id);
            assertTrue(isDeleted);
            isDeleted = newsFeedDB.deleteNotification(id);
            assertFalse(isDeleted);
        }
    }
