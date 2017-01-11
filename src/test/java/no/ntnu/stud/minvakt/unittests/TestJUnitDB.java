package no.ntnu.stud.minvakt.unittests;

/**
 * Created by evend on 1/10/2017.
 */
import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.database.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.*;


public class TestJUnitDB {
    private static ShiftDBManager shiftDB;

    @BeforeClass
    public static void DBsetUp(){
        try {
            shiftDB = new ShiftDBManager();
        }
        catch(Exception e) {
            System.err.println("Issue with databaseconnections! ");
            e.printStackTrace();
        }
    }
    @Ignore
    @Test
    public void createShift(){
        Date curDate = new Date(System.currentTimeMillis());
        Shift shift = new Shift(-1,curDate,1,false,
                false,1,1);
        int shiftId = shiftDB.createNewShift(shift);
        shiftDB.deleteShift(shiftId);
        assertTrue(shiftId != -1);
    }
}
