package no.ntnu.stud.minvakt.services;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by evend on 1/25/2017.
 */
public class DepartmentServiceTest {
    private static DepartmentService deptService;
    private HttpServletRequest request;

    @Before
    public void setUpTest() {
        request = new MockHttpServletRequest();
        deptService = new DepartmentService(request);
    }

    private void logInUser() {
        SessionService sessionService = new SessionService();
        sessionService.checkLogin(request, "email1", "password");
    }

    @Test
    public void getDepartments(){
        assertFalse(deptService.getDepartments().isEmpty());
    }
    @Test
    public void getDepartment(){
        assertTrue(deptService.getDepartment(1).getId() == 1);
    }
}
