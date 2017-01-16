package no.ntnu.stud.minvakt.database;

import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

/**
 * Created by AnitaKristineAune on 12.01.2017.
 */
public class AvailabilityDBManagerTest {
    private static AvailabilityDBManager availabilityDB;

    @BeforeClass
    public static void DBsetUp() {
        availabilityDB = new AvailabilityDBManager();
    }

    @Test
    public void getAvailabilityTest() {
        ArrayList<Integer> userList = new ArrayList<>();
        ArrayList<Integer> userListTest = new ArrayList<>();

        userList.add(0, 7);
        userList.add(1, 3);

        int res1 = 0;
        int res2 = 0;

        userListTest = availabilityDB.getAvailability(21);

        for (int i = 0; i < userList.size(); i++) {
            res1 += userList.get(i);
            res2 += userListTest.get(i);

        }
        assertEquals(res1, res2);
    }

//    @Ignore
    @Test
    public void setAvailabilityTest() {
        boolean res = availabilityDB.setAvailability(2, 22);
        boolean test = true;

        assertEquals(res, test);
    }


    @Test
    public void setAvailableFailTest() {
        boolean res = availabilityDB.setAvailability(1000, 13);
        boolean test = false;

        assertEquals(res, test);
    }
    @Test
    public void deleteAvailabilityTest() {
        boolean res = availabilityDB.deleteAvailability(2, 22);
        boolean test = true;

        assertEquals(res, test);
    }
}