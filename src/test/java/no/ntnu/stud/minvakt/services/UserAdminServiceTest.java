package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.ShiftUser;
import no.ntnu.stud.minvakt.data.User;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

/**
 * Created by evend on 1/18/2017.
 */
public class UserAdminServiceTest {
    private static UserAdminService userAdminService;
    private HttpServletRequest request;

    @Before
    public void setUpTest() {
        request = new MockHttpServletRequest();
        userAdminService = new UserAdminService(request);
    }

    private void logInUser() {
        SessionService sessionService = new SessionService();
        sessionService.checkLogin(request, "email1", "password");
    }

    @Test
    public void createUser() {
        logInUser();
        User user = new User(-1, "Even","Dalen", "hash","salt","even@mail.com","20012111", User.UserCategory.HEALTH_WORKER, 1);
        Response response = userAdminService.addUser(user);
        if (response.getStatus() == 200) {
            String rawJson = (String) response.getEntity();
            System.out.println(rawJson);
            JSONObject o = new JSONObject(rawJson);
            Integer userId = o.getInt("id");
            boolean isDeleted = userAdminService.deleteUser(userId);
            assertTrue(isDeleted);

        }
        assertTrue(response.getStatus() == 200);
    }
}
