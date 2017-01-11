package no.ntnu.stud.minvakt.services;

import org.junit.Assert;
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
    public void checkLogin() throws Exception {
        SessionService sessionService = new SessionService();
        HttpServletRequest request = new MockHttpServletRequest();
        Response response = sessionService.checkLogin(request, "validName", "validPassword");
        Assert.assertEquals(response.getStatus(), Response.Status.OK.getStatusCode());
    }

}