package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.UserDBManager;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import static org.junit.Assert.assertFalse;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by evend on 1/25/2017.
 */
public class UserServiceTest {
    private HttpServletRequest request;
    private static UserService userService;


    @Before
    public void setUpTest() {
        request = new MockHttpServletRequest();
        userService = new UserService(request);

    }
    @Test
    public void getUserBasicsCategory(){
        assertFalse(userService.getUserBasicsWithCategory(User.UserCategory.ASSISTANT).isEmpty());
    }
}