package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.ShiftUser;
import no.ntnu.stud.minvakt.data.ShiftUserBasic;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Created by evend on 1/10/2017.
 */

public class ShiftDBManagerTest {
    private static ShiftDBManager shiftDB;

    @BeforeClass
    public static void DBsetUp() {
        try {
            shiftDB = new ShiftDBManager();
        } catch (Exception e) {
            System.err.println("Issue with databaseconnections! ");
            e.printStackTrace();
        }
    }

    @Ignore
    public void createShift() {
        Date curDate = new Date(System.currentTimeMillis());
        // Shift shift = new Shift(-1, 2,curDate,1,false,
        // false,1,1);
        //     int shiftId = shiftDB.createNewShift(shift);
        //  shiftDB.deleteShift(shiftId);
        // assertTrue(shiftId != -1);
    }

    @Test
    public void addEmployeeToShift() {
        ShiftUser shiftUser = new ShiftUser(1, true, false);
        boolean statusOk = shiftDB.addEmployeeToShift(shiftUser, 2);
        if (statusOk) {
            shiftDB.deleteEmployeeFromShift(1, 2);
        }
        assertTrue(statusOk);
    }

    @Test
    public void getShiftsFromUserId() {
        ArrayList<ShiftUserBasic> result = shiftDB.getShiftWithUserId(1);
        assertTrue(result.get(0) instanceof ShiftUserBasic);
    }
}
