package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.util.ErrorInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

/**
 * Created by Audun on 11.01.2017.
 */
public class SessionServiceTest {
    private static SessionService sessionService;
    private HttpServletRequest request;


    @BeforeClass
    public static void setUpClass() {
        sessionService = new SessionService();
    }

    @Before
    public void setUpTest() {
        request = new MockHttpServletRequest();
    }


    @Test
    public void checkLoginValidCredentials() throws Exception {
        Response response = sessionService.checkLogin(request, "email1", "password");

        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        Assert.assertTrue(response.getEntity() instanceof User);

        User user = (User) response.getEntity();
        Assert.assertFalse(user.getFirstName().isEmpty());
        Assert.assertFalse(user.getLastName().isEmpty());
        //Assert.assertFalse(user.getEmail().isEmpty());
        Assert.assertTrue(user.getId() > 0);
    }

    @Test
    public void checkLoginAlreadyLoggedIn() throws Exception {
        // We assume that this part work
        sessionService.checkLogin(request, "email1", "password");

        // Try to login once more
        Response response = sessionService.checkLogin(request, "email1", "password");
        Assert.assertEquals(response.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
        Assert.assertEquals(null, response.getEntity());
    }

    @Test
    public void checkLoginInvalidPassword() throws Exception {
        Response response = sessionService.checkLogin(request, "email1", "invalid");
        Assert.assertEquals(response.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
        Assert.assertTrue(response.getEntity() instanceof ErrorInfo);
    }

    @Test
    public void checkLoginInvalidCredentials() throws Exception {
        Response response = sessionService.checkLogin(request, "invalid", "invalid");
        Assert.assertEquals(response.getStatus(), Response.Status.UNAUTHORIZED.getStatusCode());
        Assert.assertTrue(response.getEntity() instanceof ErrorInfo);
    }

    @Test
    public void validateSession() throws Exception {
        Response response = sessionService.validateSession(request);
        Assert.assertEquals(Response.Status.UNAUTHORIZED.getStatusCode(), response.getStatus());

        sessionService.checkLogin(request, "email1", "password");

        response = sessionService.validateSession(request);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void logOut() throws Exception {
        sessionService.checkLogin(request, "email1", "password");
        Response response = sessionService.logOut(request);
        Assert.assertEquals(null, request.getSession().getAttribute("session"));
    }
}