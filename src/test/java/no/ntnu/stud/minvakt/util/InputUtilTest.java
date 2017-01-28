package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.user.User;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.Response;

/**
 * Created by Audun on 28.01.2017.
 */
public class InputUtilTest {
    @Test
    public void validateUser() throws Exception {
        User badEmail = new User(-1, null, null, null, null, null, null, null, 0, 0);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), InputUtil.validateUser(badEmail).getStatus());

        User badPhone = new User(-1, null, null, null, null, "valid@example.com", null, null, 0, 0);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), InputUtil.validateUser(badPhone).getStatus());

        User badFirstName = new User(-1, null, null, null, null, "valid@example.com", "12345678", null, 0, 0);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), InputUtil.validateUser(badFirstName).getStatus());

        User badLastName = new User(-1, "First", null, null, null, "valid@example.com", "12345678", null, 0, 0);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), InputUtil.validateUser(badLastName).getStatus());

        User badWorkPercentage = new User(-1, "First", "Last", null, null, "valid@example.com", "12345678", null, -1, 0);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), InputUtil.validateUser(badWorkPercentage).getStatus());

        User valid = new User(-1, "First", "Last", null, null, "valid@example.com", "12345678", null, 0.5f, 0);
        Assert.assertNull(InputUtil.validateUser(valid));
    }

    @Test
    public void validatePassword() throws Exception {
        Assert.assertFalse(InputUtil.validatePassword("password"));
        Assert.assertFalse(InputUtil.validatePassword("PASSWORD"));
        Assert.assertFalse(InputUtil.validatePassword("password123"));
        Assert.assertFalse(InputUtil.validatePassword("PASSWORD123"));
        Assert.assertFalse(InputUtil.validatePassword("Password"));
        Assert.assertFalse(InputUtil.validatePassword("Password1"));
        Assert.assertTrue(InputUtil.validatePassword("Password12"));
    }
}