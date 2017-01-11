package no.ntnu.stud.minvakt.unittests;

/**
 * Created by evend on 1/10/2017.
 */
import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.ShiftUser;
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


public class TestJUnitREST {
    private static ShiftService shiftService;

    @BeforeClass
    public static void objectSetUp() {
        shiftService = new ShiftService();
    }

    @Test
    public void createShift(){
        Date curDate = new Date(System.currentTimeMillis());
        ArrayList<ShiftUser> shiftUsers = new ArrayList<ShiftUser>();

        shiftUsers.add(new ShiftUser(1,false,false));
        shiftUsers.add(new ShiftUser(2,true,false));

        Shift shift = new Shift(-1,2,new Date(System.currentTimeMillis()),1,1,shiftUsers);
        Response shiftResponse = shiftService.createShift(shift);
        Object entity = shiftResponse.getEntity();
        Assert.assertTrue(entity instanceof String);
        String shiftIdStr = (String) entity;
        JSONObject obj = new JSONObject(shiftIdStr);
        System.out.println(shiftIdStr);
        int shiftId = obj.getInt("id");
        //shiftService.deleteShift(shiftId);
        assertTrue(shiftId != -1);
    }

    @Test
    public void getShift(){
        assertNotNull(shiftService.getShift(1));
    }
}
