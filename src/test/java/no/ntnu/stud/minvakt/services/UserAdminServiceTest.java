package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.controller.encryption.Encryption;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.UserDBManager;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by evend on 1/18/2017.
 */
public class UserAdminServiceTest extends ServiceTest {
    private UserAdminService userAdminService;
    private UserService userService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        userAdminService = new UserAdminService(request);
        userService = new UserService(request);
    }

    @Test
    public void createUser() {
        logInAdmin();
        User user = new User(-1, "Even","Dalen", "hash","salt","system.minvakt@gmail.com","20012111", User.UserCategory.HEALTH_WORKER, 1,1);
        Response response = userAdminService.addUser(user);
        if (response.getStatus() == 200) {
            String rawJson = (String) response.getEntity();
            JSONObject o = new JSONObject(rawJson);
            Integer userId = o.getInt("id");
            boolean isDeleted = userAdminService.deleteUser(userId);
            assertTrue(isDeleted);

        }
        assertTrue(response.getStatus() == 200);
    }
    @Test
    public void changePassword(){
        logInUser();
        try {
            Response response = userService.changePassword("password", "Password12");
            assertTrue(response.getStatus() == 200);
            response = userService.changePassword("passwor", "password");
            assertTrue(response.getStatus() != 200);
        }
        finally {
            // Reset password
            new UserDBManager().setNewPassword(userService.getSession().getUser().getId(), new Encryption().passEncoding("password"));
        }
    }
}
