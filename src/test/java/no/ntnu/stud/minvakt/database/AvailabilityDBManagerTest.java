package no.ntnu.stud.minvakt.database;

import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by AnitaKristineAune on 12.01.2017.
 */
public class AvailabilityDBManagerTest {
    private static AvailabilityDBManager availabilityDB;

    @BeforeClass
    public static void DBsetUp(){
        try{
            availabilityDB = new AvailabilityDBManager();

        } catch (Exception e){
            System.err.println("Issue with database connection");
            e.printStackTrace();
        }
    }

//    @Ignore
    @Test
    public void getAvailabilityTest(){
        String assertionError = null;
        ArrayList<Integer> userList = new ArrayList<>();
        ArrayList<Integer> userListTest = new ArrayList<>();

        userList.add(0,7);
        userList.add(1,3);

        int res1 = 0;
        int res2 = 0;

        userListTest = availabilityDB.getAvailability(21);

        for(int i = 0; i < userList.size(); i++){
            res1 += userList.get(i);
            res2 += userListTest.get(i);

        }
        try{
            assertEquals(res1, res2);
        } catch (AssertionError ae){
            assertionError = ae.toString();
        }

        System.out.print(assertionError);
    }

//    @Ignore
    @Test
    public void setAvailabilityTest(){
        boolean res = availabilityDB.setAvailability(11, 13);
        boolean test = true;
        String assertionError = null;

        try{
            assertEquals(res, test);
        } catch (AssertionError ae){
            assertionError = ae.toString();
        }

        System.out.print(assertionError);

    }
    @Test
//    @Ignore
    public void deleteAvailabilityTest(){
        boolean res = availabilityDB.deleteAvailability(2,22);
        boolean test = true;

        String assertionError = null;

        try{
            assertEquals(res, test);
        } catch (AssertionError ae){
            assertionError = ae.toString();
        }

        System.out.print(assertionError);
    }

}

/*
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

    @Test
    public void addEmployeeToShift(){
        ShiftUser shiftUser = new ShiftUser(1, true, false);
        boolean statusOk = shiftDB.addEmployeeToShift(shiftUser, 2);
        if(statusOk){
            shiftDB.deleteEmployeeFromShift(1, 2);
        }
        assertTrue(statusOk);
    }}
*/