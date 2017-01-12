package no.ntnu.stud.minvakt.services;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.core.Response;

/**
 * Created by Marit on 12.01.2017.
 */
public class AvailabilityServiceTest {
    private static AvailabilityService availabilityService;

    @BeforeClass
    public static void objectSetUp() {
        availabilityService = new AvailabilityService();
    }

    @Test
    public void checkGetAvailability(){
        Response response = availabilityService.getAvailability(19);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
    @Test
    public void checkGetAvailabilityEmptyShift(){
        Response response = availabilityService.getAvailability(10);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }

    @Test
    public void checkSetAvailability(){
        Response response = availabilityService.setAvailability(3,19);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void deleteAvailability(){
        Response response = availabilityService.deleteAvailability(10,19);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}
