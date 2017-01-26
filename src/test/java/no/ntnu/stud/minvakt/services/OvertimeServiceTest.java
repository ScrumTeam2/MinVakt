package no.ntnu.stud.minvakt.services;

/**
 * Created by Marit on 23.01.2017.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import no.ntnu.stud.minvakt.data.Overtime;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.*;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class OvertimeServiceTest extends ServiceTest {
    private OvertimeService overtimeService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        overtimeService = new OvertimeService(request);
    }

    @Test
    public void setOvertimeTest() throws Exception {
        logInUser();
        int userId =1;
        int shiftId = 22;
        int startTime = 900;
        int minutes = 15;
        Overtime overtime = new Overtime(0, shiftId, startTime, minutes, false);
        Response response = overtimeService.setOvertime(overtime);
        Assert.assertEquals(200, response.getStatus());
        overtimeService.deleteOvertime(userId, shiftId, startTime);
    }

    @Test
    public void getOvertimeTest() throws Exception {
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
    public void deleteOvertimeTest() throws Exception{
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
