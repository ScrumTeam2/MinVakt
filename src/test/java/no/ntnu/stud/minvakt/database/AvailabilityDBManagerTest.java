package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.UserAvailableShifts;
import no.ntnu.stud.minvakt.data.shift.ShiftAvailable;
import no.ntnu.stud.minvakt.data.user.UserBasicWorkHours;
import org.junit.*;

import java.sql.Date;
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

    @Test
    public void getAvailabilityForUser() throws Exception {
        int userId = 1;
        UserAvailableShifts user = availabilityDB.getAvailabilityForUser(userId);
        ArrayList<Integer> shifts = user.getShifts();
        int shiftID = shifts.get(0);
        Assert.assertEquals(41, shiftID);
    }

    @Test
    public void getAvailabilityUserBasic() throws Exception {
        int shiftId = 41;
        ArrayList<UserBasicWorkHours> user = availabilityDB.getAvailabilityUserBasic(shiftId);
        int userId = user.get(0).getId();
        int expId = 1;

        Assert.assertEquals(expId, userId);
    }

    @Test
    public void getShiftsForDate() throws Exception {
        int userId = 1;
        int daysForward = 1;
        String fromDateString = "2017-02-12";
        Date date = Date.valueOf(fromDateString);
        ArrayList<ShiftAvailable> shifts = availabilityDB.getShiftsForDate(daysForward, userId, date);
        Assert.assertEquals(61, shifts.get(0).getShiftId());
    }
}