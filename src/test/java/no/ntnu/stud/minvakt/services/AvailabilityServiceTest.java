package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.UserAvailableShifts;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shift.ShiftAvailable;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.Response;

import java.sql.Date;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class AvailabilityServiceTest extends ServiceTest {

    private AvailabilityDBManager availDB;

    private static AvailabilityService availabilityService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        availDB = new AvailabilityDBManager();
        availabilityService = new AvailabilityService(request);
    }

    @Test
    public void getAvailabilityUser() throws Exception {
        logInUser();
        UserAvailableShifts user = availabilityService.getAvailabilityUser();
        ArrayList<Integer> shift = user.getShifts();
        int expRes = 41;
        int actual = shift.get(0);
        Assert.assertEquals(expRes, actual);
    }

    @Test
    public void getAvailableUsersForShift() throws Exception {
        logInAdmin();
        int shiftId = 43;
        String categoryString = "ASSISTANT";
        boolean onlyThisCategory = true;
        Response res = availabilityService.getAvailableUsersForShift(shiftId, categoryString, onlyThisCategory);

        assertTrue(res.getStatus() == 200);
    }

    @Test
    public void getShifts() throws Exception {
        logInUser();
        int daysForward = 1;
        String fromDateString = "2017-02-12";
        Date date = Date.valueOf(fromDateString);
        ArrayList<ShiftAvailable> shifts = availabilityService.getShifts(daysForward, date);
        Assert.assertEquals(61, shifts.get(0).getShiftId());

    }

    @Ignore
    public void setAvailability() throws Exception {
        String list = "";
        availabilityService.setAvailability(list);
    }

    @Ignore
    public void delAvailability() throws Exception {
        String list ="";
        availabilityService.delAvailability(list);
    }
}
