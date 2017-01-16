package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.util.ErrorInfo;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import static org.junit.Assert.*;

/**
 * Created by Audun on 11.01.2017.
 */
public class SessionServiceTest {
    @Test
    public void checkLoginValidCredentials() throws Exception {
        SessionService sessionService = new SessionService();
        HttpServletRequest request = new MockHttpServletRequest();
        Response response = sessionService.checkLogin(request, "email1", "password");
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
        Assert.assertTrue(response.getEntity() instanceof User);
    }

    @Ignore
    public void checkLoginAlreadyLoggedIn() throws Exception {
        // We assume that this part work
        SessionService sessionService = new SessionService();
        HttpServletRequest request = new MockHttpServletRequest();
        sessionService.checkLogin(request, "email1", "password");

        // Try to login once more
        Response response = sessionService.checkLogin(request, "email1", "password");
        Assert.assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
    }

    @Ignore
    public void checkLoginInvalidPassword() throws Exception {
        SessionService sessionService = new SessionService();
        HttpServletRequest request = new MockHttpServletRequest();
        Response response = sessionService.checkLogin(request, "email1", "invalid");
        Assert.assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
        Assert.assertTrue(response.getEntity() instanceof ErrorInfo);
    }

    @Ignore
    public void checkLoginInvalidCredentials() throws Exception {
        SessionService sessionService = new SessionService();
        HttpServletRequest request = new MockHttpServletRequest();
        Response response = sessionService.checkLogin(request, "invalid", "invalid");
        Assert.assertEquals(response.getStatus(), Response.Status.FORBIDDEN.getStatusCode());
        Assert.assertTrue(response.getEntity() instanceof ErrorInfo);
    }
}