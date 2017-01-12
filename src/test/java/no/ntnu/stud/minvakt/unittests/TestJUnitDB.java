package no.ntnu.stud.minvakt.unittests;

/**
 * Created by evend on 1/10/2017.
 */
import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.ShiftUser;
import no.ntnu.stud.minvakt.data.ShiftUserBasic;
import no.ntnu.stud.minvakt.database.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Date;
import java.util.ArrayList;

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
    @Test
    public void createShift(){
        ArrayList<ShiftUser> shiftUsers = new ArrayList<>();
        shiftUsers.add(new ShiftUser(1,false,false));
        Shift shift = new Shift(-1,1, new Date(System.currentTimeMillis()), 1,1, shiftUsers);
        int shiftId = shiftDB.createNewShift(shift);
        if(shiftId != 0){
            boolean ok = shiftDB.deleteShift(shiftId);
            assertTrue(ok);
        }
        assertTrue(shiftId != 0);
    }
    @Test
    public void addEmployeeToShift(){
        ShiftUser shiftUser = new ShiftUser(1, true, false);
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
