package no.ntnu.stud.minvakt.services;

import no.ntnu.stud.minvakt.database.AvailabilityDBManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;

/**
 * Created by Marit on 12.01.2017.
 */
public class AvailabilityServiceTest extends ServiceTest {
    private ShiftService shiftService;
    private AvailabilityDBManager availDB;

    private static AvailabilityService availabilityService;

    @Override
    @Before
    public void setUp() {
        super.setUp();
        availDB = new AvailabilityDBManager();
        availabilityService = new AvailabilityService(request);
    }

    /*
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
    */

    @Test
    public void deleteAvailability(){
        availDB.setAvailability(3,19);
        Response response = availabilityService.deleteAvailability(3,19);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void checkSetAvailability(){
        Response response = availabilityService.setAvailability(3,19);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

}
