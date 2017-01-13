package no.ntnu.stud.minvakt.services;

/**
 * Created by evend on 1/10/2017.
 */
import com.google.gson.Gson;
import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.ShiftUser;
import no.ntnu.stud.minvakt.data.ShiftUserBasic;
import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.database.*;
import no.ntnu.stud.minvakt.services.ShiftService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.json.*;


import javax.ws.rs.core.Response;
import java.sql.Date;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class ShiftServiceTest {
    private static ShiftService shiftService;

    @BeforeClass
    public static void objectSetUp() {
        shiftService = new ShiftService();
    }

    @Ignore
    public void createShift() {
        ArrayList<ShiftUser> shiftUsers = new ArrayList<>();
        shiftUsers.add(new ShiftUser(1, "Ole", User.UserCategory.HEALTH_WORKER, false, false));
        Shift shift = new Shift(-1, 1, new Date(System.currentTimeMillis()), 1, 1, shiftUsers);
        Response response = shiftService.createShift(shift);
        if (response.getStatus() == 200) {
            String rawJson = response.readEntity(String.class);
            Gson gson = new Gson();
            Integer shiftId = gson.fromJson(rawJson, Integer.class);
            Response delResponse = shiftService.deleteShift(shiftId);
            assertTrue(delResponse.getStatus() == 200);
        }
        assertTrue(response.getStatus() == 200);
    }

    @Test
    public void getShift() {
        assertNotNull(shiftService.getShift(1));
    }

    @Ignore
    public void addEmployeeToShift() {
        ShiftUser shiftUser = new ShiftUser(1, "ole",User.UserCategory.HEALTH_WORKER, true, false);
        Response statusOk = shiftService.addEmployeeToShift(shiftUser, 2);
        if (statusOk.getStatus() == 200) {
            statusOk = shiftService.deleteEmployeeFromShift(1, 2);
        }
        assertTrue(statusOk.getStatus() == 200);
    }

    @Test
    public void getEmployeeBasicsWithUserId() {
        ArrayList<ShiftUserBasic> shiftUserBasics = shiftService.getUserBasicFromId(1);
        assertFalse(shiftUserBasics.isEmpty());
    }
}
