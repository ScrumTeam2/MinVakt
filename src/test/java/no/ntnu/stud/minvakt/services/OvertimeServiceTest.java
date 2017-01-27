package no.ntnu.stud.minvakt.services;

/**
 * Created by Marit on 23.01.2017.
 */

import no.ntnu.stud.minvakt.data.Overtime;
import org.junit.*;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

public class OvertimeServiceTest extends ServiceTest {
    private OvertimeService overtimeService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        overtimeService = new OvertimeService(request);
    }

    @Test
    public void setOvertimeUser() throws Exception {
        logInUser();
        int userId =1;
        int shiftId = 22;
        int startTime = 900;
        int minutes = 15;
        Overtime overtime = new Overtime(1, shiftId, startTime, minutes, false);
        Response response = overtimeService.setOvertime(overtime);
        Assert.assertEquals(200, response.getStatus());
        overtimeService.deleteOvertime(userId, shiftId, startTime);
    }
    @Test
    public void setOvertimeAdmin() throws Exception {
        logInAdmin();
        int userId =1;
        int shiftId = 22;
        int startTime = 900;
        int minutes = 15;
        Overtime overtime = new Overtime(userId, shiftId, startTime, minutes, false);
        Response response = overtimeService.setOvertime(overtime);
        Assert.assertEquals(200, response.getStatus());
        overtimeService.deleteOvertime(userId, shiftId, startTime);
    }

    @Test
    public void getOvertime() throws Exception {
        logInUser();

        Response response = overtimeService.getOvertimeByUserId();
        ArrayList<Overtime> actual = (ArrayList<Overtime>) response.getEntity();

        ArrayList<Overtime> expRes = new ArrayList<>();
        expRes.add(new Overtime(1,16,960, 60, true));
        expRes.add(new Overtime(1,37,960,-80, false));

        Assert.assertEquals(expRes.get(0), actual.get(0));
        Assert.assertEquals(expRes.get(1), actual.get(1));
    }
    @Test
    public void getOvertimeByShiftId() throws Exception {
        int userId = 1;
        int shiftId = 16;
        Response response = overtimeService.getOvertimeByShiftId(shiftId, userId);
        ArrayList<Overtime> actual = (ArrayList<Overtime>) response.getEntity();

        ArrayList<Overtime> expRes = new ArrayList<>();
        expRes.add(new Overtime(1,16,960, 60, true));
        Assert.assertEquals(expRes.get(0), actual.get(0));
    }

    @Test
    public void deleteOvertime() throws Exception{
        logInUser();
        int userId = 1;
        int shiftId = 8;
        int startTime = 900;
        int minutes = 15;
        Overtime overtime = new Overtime(userId, shiftId, startTime, minutes, false);
        overtimeService.setOvertime(overtime);
        Response response = overtimeService.deleteOvertime(userId, shiftId, startTime);
        Assert.assertEquals(200, response.getStatus());
    }
}
