package no.ntnu.stud.minvakt.unittests;

/**
 * Created by evend on 1/10/2017.
 */
import no.ntnu.stud.minvakt.database.*;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.*;


public class TestJUnitDB {
    private static ShiftDBManager shift;

    @BeforeClass
    public static void DBsetUp(){
        try {
            shift = new ShiftDBManager();
        }
        catch(Exception e) {
            System.err.println("Issue with databaseconnections! ");
            e.printStackTrace();
        }
    }
    @Test
    public void createShift(){
        Date curDate = new Date(System.currentTimeMillis());
        int shiftID = shift.createNewShift(curDate,10,20,false,
                false,1,1);
        shift.deleteShift(shiftID);
        assertTrue(shiftID != -1);
    }
}
