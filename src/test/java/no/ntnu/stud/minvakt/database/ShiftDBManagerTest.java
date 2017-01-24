package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.shift.*;
import no.ntnu.stud.minvakt.data.user.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import no.ntnu.stud.minvakt.data.shift.ShiftAvailable;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
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

    @Test
    public void createShift(){
        ArrayList<ShiftUser> shiftUsers = new ArrayList<>();
        shiftUsers.add(new ShiftUser(1,"ole", User.UserCategory.HEALTH_WORKER,false,false));
        Shift shift = new Shift(-1,1, java.sql.Date.valueOf("1995-01-01"), 1,1, shiftUsers);
        int shiftId = shiftDB.createNewShift(shift);
        if(shiftId != 0){
            boolean ok = shiftDB.deleteShift(shiftId);
            assertTrue(ok);
        }
        assertTrue(shiftId != 0);
    }

    @Test
    public void addEmployeeToShift(){
        ShiftUser shiftUser = new ShiftUser(1, "ole", User.UserCategory.HEALTH_WORKER, true, false);
        boolean statusOk = shiftDB.addEmployeeToShift(shiftUser, 9);
        if(statusOk){
            shiftDB.deleteEmployeeFromShift(1, 9);
        }
        assertTrue(statusOk);
    }

    @Test
    public void replaceEmployeeOnShift() {
        final int shiftId = 2;
        final int oldUserId = 1;
        final int newUserId = 15;

        ShiftUser shiftUser = new ShiftUser(oldUserId, "ole", User.UserCategory.HEALTH_WORKER, true, false);
        ShiftUser newUser = null;

        boolean statusOk = shiftDB.addEmployeeToShift(shiftUser, shiftId);
        if(statusOk){
            boolean replaceOK = shiftDB.replaceEmployeeOnShift(shiftId, oldUserId, newUserId);
            if(replaceOK) {
                newUser = shiftDB.getUserFromShift(newUserId, shiftId);
                shiftDB.deleteEmployeeFromShift(newUserId, shiftId);
            } else {
                shiftDB.deleteEmployeeFromShift(oldUserId, shiftId);
            }

            assertTrue(replaceOK);
            Assert.assertNotNull(newUser);
            Assert.assertEquals(newUserId, newUser.getUserId());
        }
        assertTrue(statusOk);
    }

    //Skaper problemer med at det ikke er koblet noen skift i databasen
    @Test
    public void getShiftsFromUserId(){
        ArrayList<ShiftUserBasic> result = shiftDB.getShiftWithUserId(1,new java.sql.Date(System.currentTimeMillis()));
        assertTrue(result.get(0) instanceof ShiftUserBasic);
    }


    // int getShiftMinutes(int userId, Date startDate, Date endDate)
    @Test
    public void getNumberOfShiftsTest(){
        String stringDate1 = "2017-01-01";
        String stringDate2 = "2017-01-31";
        java.sql.Date date1 = java.sql.Date.valueOf(stringDate1);
        java.sql.Date date2 = java.sql.Date.valueOf(stringDate2);

        int res = shiftDB.getNumberOfShifts(10, date1, date2);
        int expRes = 3;
        assertEquals(expRes, res);
    }

    @Test
    public void setShiftChangeTest(){
        boolean res = shiftDB.setShiftChange(4, 1);
        boolean expRes = true;
        assertEquals(expRes, res);
    }

    @Test
    public void getShifts(){
        ArrayList<ShiftUserAvailability> status = shiftDB.getShifts(300, 1,new java.sql.Date(System.currentTimeMillis()));
        assertFalse(status.isEmpty());

    }

    @Test
    public void setStaffNumberOnShift() {
        assertFalse(shiftDB.setStaffNumberOnShift(-1, 10));
        assertTrue(shiftDB.setStaffNumberOnShift(1, 4));
    }

    @Test
    public void hasAnyShiftsInPeriod() throws Exception {
        assertFalse(shiftDB.hasAnyShiftsInPeriod(LocalDate.parse("2014-01-01"), LocalDate.parse("2014-02-14")));
        assertTrue(shiftDB.hasAnyShiftsInPeriod(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-02-14")));
    }

    @Test
    public void setValidAbsence(){
        assertTrue(shiftDB.setValidAbsence(1,4,true));
        assertTrue(shiftDB.setValidAbsence(1,4,false));
    }

    @Test
    public void getAvailableShiftsTest(){
        ArrayList<ShiftAvailable> resList = shiftDB.getAvailableShifts();
        assertFalse(resList.isEmpty());
    }
    @Test
    public void getUsersFromShift(){
        assertFalse(shiftDB.getUsersFromShift(1).isEmpty());
    }
    @Test
    public void setResponsibleUser(){
        assertTrue(shiftDB.setResponsibleUser(1,1,true));
        assertTrue(shiftDB.setResponsibleUser(1,1,false));

    }
}

