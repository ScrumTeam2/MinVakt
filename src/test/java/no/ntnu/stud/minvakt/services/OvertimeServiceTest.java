package no.ntnu.stud.minvakt.services;

/**
 * Created by Marit on 23.01.2017.
 */

import no.ntnu.stud.minvakt.data.Overtime;
import org.json.JSONObject;
import org.junit.*;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.sql.Date;

import static org.junit.Assert.assertTrue;

public class OvertimeServiceTest {
    private static OvertimeService overtimeService;
    private HttpServletRequest request;

    @Before
    public void setUpTest() {
        request = new MockHttpServletRequest();
        overtimeService = new OvertimeService(request);
    }

    private void logInUser() {
        SessionService sessionService = new SessionService();
        sessionService.checkLogin(request, "email1", "password");
    }

    @Test
    public void setOvertimeTest() throws Exception {
        int userId = 10;
        int shiftId = 13;
        int startTime = 900;
        int minutes = 15;
        Overtime overtime = new Overtime(userId, shiftId, startTime, minutes, false);
        Response response = overtimeService.setOvertime(overtime);
        Assert.assertEquals(200, response.getStatus());
        overtimeService.deleteOvertime(userId, shiftId, startTime);
    }

    @Ignore
    public void getOvertimeTest() throws Exception {
        int userId = 4;
        Response response = overtimeService.getOvertimeByUserId(userId);
        Overtime[] expRes = new Overtime[2];

        expRes[0] = new Overtime(4,28,780,-120,false);
        expRes[1] = new Overtime(4, 61, 840, -60, false);

        //Assert.assertEquals(expRes[0], overtime[0]);
        //Assert.assertEquals(expRes[1], overtime[1]);

        /*
         user_id, shift_id, start_time, minutes, approved
	     	4 	    28  	    780 	-120 	    0
	    	4 	    61 	        840 	 -60 	    0
         */

    }

    @Test
    public void deleteOvertimeTest() throws Exception{
        int userId = 10;
        int shiftId = 13;
        int startTime = 900;
        int minutes = 15;
        Overtime overtime = new Overtime(userId, shiftId, startTime, minutes, false);
        overtimeService.setOvertime(overtime);
        Response response = overtimeService.deleteOvertime(userId, shiftId, startTime);
        Assert.assertEquals(200, response.getStatus());
    }
}
