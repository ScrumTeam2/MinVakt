package no.ntnu.stud.minvakt.database;

/**
 * Created by evend on 1/10/2017.
 */
import org.junit.BeforeClass;
import org.junit.Ignore;

import java.sql.Date;


public class ShiftDBManagerTest {
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
    public void createShift(){
        Date curDate = new Date(System.currentTimeMillis());
       // Shift shift = new Shift(-1, 2,curDate,1,false,
               // false,1,1);
   //     int shiftId = shiftDB.createNewShift(shift);
      //  shiftDB.deleteShift(shiftId);
       // assertTrue(shiftId != -1);
    }
}
