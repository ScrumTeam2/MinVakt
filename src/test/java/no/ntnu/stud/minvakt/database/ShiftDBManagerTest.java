package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.data.shift.*;
import no.ntnu.stud.minvakt.data.user.User;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import no.ntnu.stud.minvakt.data.shift.ShiftAvailable;

import java.sql.Date;
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
        shiftUsers.add(new ShiftUser(1,"ole", User.UserCategory.HEALTH_WORKER,false,0, -1));
        Shift shift = new Shift(-1,1, java.sql.Date.valueOf("1995-01-01"), 1,1, shiftUsers, false);
        int shiftId = shiftDB.createNewShift(shift);
        if(shiftId != 0){
            boolean ok = shiftDB.deleteShift(shiftId);
            assertTrue(ok);
        }
        assertTrue(shiftId != 0);
    }

    @Test
    public void addEmployeeToShift(){
        ShiftUser shiftUser = new ShiftUser(1, "ole", User.UserCategory.HEALTH_WORKER, true, 0, -1);
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

        ShiftUser shiftUser = new ShiftUser(oldUserId, "ole", User.UserCategory.HEALTH_WORKER, true, 0, -1);
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
        ArrayList<ShiftUserAvailability> status = shiftDB.getShifts(300, 1,new java.sql.Date(System.currentTimeMillis()),1);
        assertFalse(status.isEmpty());

    }

    @Test
    public void setStaffNumberOnShift() {
        assertFalse(shiftDB.setStaffNumberOnShift(-1, 10));
        assertTrue(shiftDB.setStaffNumberOnShift(1, 4));
    }

    @Test
    public void hasAnyShiftsInPeriod() throws Exception {
        assertFalse(shiftDB.hasAnyShiftsInPeriod(LocalDate.parse("2014-01-01"), LocalDate.parse("2014-02-14"), 1));
        assertTrue(shiftDB.hasAnyShiftsInPeriod(LocalDate.parse("2017-01-01"), LocalDate.parse("2017-02-14"), 1));
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
    public void approveShifts() throws Exception {
        int shiftId = shiftDB.createNewShift(new Shift(-1, 0, Date.valueOf("2010-01-01"), Shift.ShiftType.DAY, 1, new ArrayList<>(), false));
        Assert.assertTrue(shiftId > 0);
        try {
            Shift shift = shiftDB.getShift(shiftId);
            Assert.assertNotNull(shift.isApproved());
            Assert.assertFalse(shift.isApproved());

            Assert.assertTrue(shiftDB.approveShifts(new int[]{shiftId}));
            shift = shiftDB.getShift(shiftId);
            Assert.assertTrue(shift.isApproved());
        } finally {
            shiftDB.deleteShift(shiftId);
        }
    }

    @Test
    public void bulkInsertShifts() throws Exception {
        ArrayList<Shift> shifts = new ArrayList<>();

        // Create shifts
        shifts.add(new Shift(-1, 0, Date.valueOf("2010-01-01"), Shift.ShiftType.DAY, 1, new ArrayList<>(), false));
        shifts.add(new Shift(-1, 0, Date.valueOf("2010-01-01"), Shift.ShiftType.EVENING, 1, new ArrayList<>(), false));
        shifts.add(new Shift(-1, 0, Date.valueOf("2010-01-01"), Shift.ShiftType.NIGHT, 1, new ArrayList<>(), false));

        // Add user to a shift
        shifts.get(0).getShiftUsers().add(new ShiftUser(1, "", User.UserCategory.ADMIN, true, 0, -1));
        Assert.assertTrue(shiftDB.bulkInsertShifts(shifts));
        try {
            for (Shift shift : shifts) {
                Shift selectedShift = shiftDB.getShift(shift.getId());
                if (selectedShift.getId() == shifts.get(0).getId()) {
                    Assert.assertEquals(1, selectedShift.getShiftUsers().size());
                    Assert.assertEquals(1, selectedShift.getShiftUsers().get(0).getUserId());
                }
                Assert.assertNotNull(selectedShift);
            }
        } finally {
            for (Shift shift : shifts) {
                shiftDB.deleteShift(shift.getId());
            }
        }
    }

    @Test
    public void setResponsibleUser(){
        assertTrue(shiftDB.setResponsibleUser(1,1,true));
        assertTrue(shiftDB.setResponsibleUser(1,1,false));
    }
}

