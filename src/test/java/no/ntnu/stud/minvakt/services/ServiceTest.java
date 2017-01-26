package no.ntnu.stud.minvakt.services;

import org.junit.Before;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * Base class for all REST test classes. Has methods for logging in and creating a request object
 */
public abstract class ServiceTest {
    protected HttpServletRequest request;

    @Before
    public void setUp() {
        request = new MockHttpServletRequest();
    }

    protected void logInUser() {
        SessionService sessionService = new SessionService();
        sessionService.checkLogin(request, "email1", "password");
    }

    protected void logInAdmin() {
        SessionService sessionService = new SessionService();
        sessionService.checkLogin(request, "admin", "password");
    }
}
