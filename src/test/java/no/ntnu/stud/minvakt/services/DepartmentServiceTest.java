package no.ntnu.stud.minvakt.services;

import org.junit.Before;
import org.junit.Test;
import javax.ws.rs.NotAuthorizedException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by evend on 1/25/2017.
 */
public class DepartmentServiceTest extends ServiceTest {
    private static DepartmentService deptService;

    @Before
    @Override
    public void setUp() {
        super.setUp();
        deptService = new DepartmentService(request);
    }

    @Test
    public void getDepartments(){
        logInUser();
        assertFalse(deptService.getDepartments().isEmpty());
    }

    @Test(expected = NotAuthorizedException.class)
    public void getDepartmentsNoLogin(){
        assertFalse(deptService.getDepartments().isEmpty());
    }

    @Test
    public void getDepartment(){
        logInUser();
        assertTrue(deptService.getDepartment(1).getId() == 1);
    }

    @Test(expected = NotAuthorizedException.class)
    public void getDepartmentNoLogin(){
        assertTrue(deptService.getDepartment(1).getId() == 1);
    }
}
