package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.ShiftUser;
import no.ntnu.stud.minvakt.data.ShiftUserBasic;
import no.ntnu.stud.minvakt.data.User;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by evend on 1/10/2017.
 */

public class ShiftDBManagerTest {
    private static ShiftDBManager shiftDB;

    @BeforeClass
    public static void DBsetUp() {
        shiftDB = new ShiftDBManager();
    }

    @Ignore
    public void createShift(){
        ArrayList<ShiftUser> shiftUsers = new ArrayList<>();
        shiftUsers.add(new ShiftUser(1,"ole", User.UserCategory.HEALTH_WORKER,false,false));
        Shift shift = new Shift(-1,1, new java.sql.Date(System.currentTimeMillis()), 1,1, shiftUsers);
        int shiftId = shiftDB.createNewShift(shift);
        if(shiftId != 0){
            boolean ok = shiftDB.deleteShift(shiftId);
            assertTrue(ok);
        }
        assertTrue(shiftId != 0);
    }
    @Ignore
    public void addEmployeeToShift(){
        ShiftUser shiftUser = new ShiftUser(1, "ole", User.UserCategory.HEALTH_WORKER, true, false);
        boolean statusOk = shiftDB.addEmployeeToShift(shiftUser, 2);
        if(statusOk){
            shiftDB.deleteEmployeeFromShift(1, 2);
        }
        assertTrue(statusOk);
    }
    @Test
    public void getShiftsFromUserId(){
        ArrayList<ShiftUserBasic> result = shiftDB.getShiftWithUserId(1);
        assertTrue(result.get(0) instanceof ShiftUserBasic);
    }

}

