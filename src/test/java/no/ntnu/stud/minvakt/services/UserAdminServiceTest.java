package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.user.User;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

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
        User user = new User(-1, "Even","Dalen", "hash","salt","email","phnumber", User.UserCategory.HEALTH_WORKER, 1);
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
