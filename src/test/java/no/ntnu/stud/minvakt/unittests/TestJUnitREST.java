package no.ntnu.stud.minvakt.unittests;

/**
 * Created by evend on 1/10/2017.
 */
import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.database.*;
import no.ntnu.stud.minvakt.services.ShiftService;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.json.*;


import javax.ws.rs.core.Response;
import java.sql.Date;

import static org.junit.Assert.*;


public class TestJUnitREST {
    private static ShiftService shiftService;

    @BeforeClass
    public static void objectSetUp() {
        shiftService = new ShiftService();
    }

    @Test
    @Ignore
    public void createShift(){
        Date curDate = new Date(System.currentTimeMillis());
        Response  shiftResponse = shiftService.createShift(curDate, 1, false,false,1,1);
        String shiftIdStr = shiftResponse.readEntity(String.class);
        System.out.println(shiftIdStr);
        int shiftId = Integer.parseInt(shiftIdStr);
        //shiftService.deleteShift(shiftId);
        assertTrue(shiftId != -1);
    }
}
