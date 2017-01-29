package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.AvailabilityDBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import javax.ws.rs.core.Response;

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

    @Ignore
    public void getAvailabilityUser() throws Exception {
        logInUser();
        availabilityService.getAvailabilityUser();
    }

    @Ignore
    public void setAvailability() throws Exception {
        String list = "";
        availabilityService.setAvailability(list);
    }

    @Ignore
    public void getShifts() throws Exception {
        int daysForward = 0;
        java.sql.Date date = null;
        availabilityService.getShifts(daysForward, date);

    }

    @Ignore
    public void delAvailability() throws Exception {
        String list ="";
        availabilityService.delAvailability(list);
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
}
