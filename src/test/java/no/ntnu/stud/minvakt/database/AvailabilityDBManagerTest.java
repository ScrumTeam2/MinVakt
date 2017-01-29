package no.ntnu.stud.minvakt.database;

import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by AnitaKristineAune on 12.01.2017.
 */
public class AvailabilityDBManagerTest {
    private static AvailabilityDBManager availabilityDB;

    @BeforeClass
    public static void DBsetUp() {
        availabilityDB = new AvailabilityDBManager();
    }

    @Test
    public void setAvailabilityTest() {
        boolean res = availabilityDB.setAvailability(2, 22);
        boolean test = true;

        if(test) {
            availabilityDB.deleteAvailability(2, 22); // Clean up
        }
        assertEquals(res, test);
    }

    @Test
    public void setAvailableFailTest() {
        boolean res = availabilityDB.setAvailability(1000, 13);
        boolean test = false;

        if(test) {
            availabilityDB.deleteAvailability(1000, 13); // Clean up
        }

        assertEquals(res, test);
    }

    @Test
    public void deleteAvailabilityTest() {
        availabilityDB.setAvailability(2, 22);
        boolean res = availabilityDB.deleteAvailability(2, 22);
        boolean test = true;

        assertEquals(res, test);
    }

    @Ignore
    public void getAvailabilityForUser() throws Exception {

    }

    @Ignore
    public void getAvailabilityUserBasic() throws Exception {

    }

    @Ignore
    public void getShiftsForDate() throws Exception {

    }
}