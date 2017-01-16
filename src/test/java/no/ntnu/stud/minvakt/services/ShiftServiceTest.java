package no.ntnu.stud.minvakt.services;

/**
 * Created by evend on 1/10/2017.
 */
import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.ShiftUser;
import no.ntnu.stud.minvakt.data.ShiftUserBasic;
import no.ntnu.stud.minvakt.data.User;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class ShiftServiceTest {
    private static ShiftService shiftService;
    private HttpServletRequest request;

    @Before
    public void setUpTest() {
        request = new MockHttpServletRequest();
        shiftService = new ShiftService(request);
    }

    private void logInUser() {
        SessionService sessionService = new SessionService();
        sessionService.checkLogin(request, "email1", "password");
    }

    @Test
    public void createShift() {
        logInUser();
        ArrayList<ShiftUser> shiftUsers = new ArrayList<>();
        shiftUsers.add(new ShiftUser(1, "Ole", User.UserCategory.HEALTH_WORKER, false, false));
        Shift shift = new Shift(-1, 1, new Date(System.currentTimeMillis()), 1, 1, shiftUsers);
        Response response = shiftService.createShift(shift);
        if (response.getStatus() == 200) {
            String rawJson = (String) response.getEntity();
            JSONObject o = new JSONObject(rawJson);
            Integer shiftId = o.getInt("id");
            Response delResponse = shiftService.deleteShift(shiftId);
            assertTrue(delResponse.getStatus() == 200);
        }
        assertTrue(response.getStatus() == 200);
    }

    @Test
    public void getShift() {
        logInUser();
        assertNotNull(shiftService.getShift(1));
    }

    @Test
    public void addEmployeeToShift() {
        logInUser();
        ShiftUser shiftUser = new ShiftUser(1, "ole",User.UserCategory.HEALTH_WORKER, true, false);
        Response statusOk = shiftService.addEmployeeToShift(shiftUser, 2);
        if (statusOk.getStatus() == 200) {
            statusOk = shiftService.deleteEmployeeFromShift(1, 2);
        }
        assertTrue(statusOk.getStatus() == 200);
    }

    @Test
    public void getEmployeeBasicsWithUserId() {
        logInUser();
        ArrayList<ShiftUserBasic> shiftUserBasics = shiftService.getUserBasicFromId();
        assertFalse(shiftUserBasics.isEmpty());
    }
}
