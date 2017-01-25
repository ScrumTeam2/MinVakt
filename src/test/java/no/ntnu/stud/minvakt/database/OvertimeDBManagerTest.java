package no.ntnu.stud.minvakt.database;
import no.ntnu.stud.minvakt.data.Overtime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import java.sql.Date;

/**
 * Created by AnitaKristineAune on 13.01.2017.
 */
public class OvertimeDBManagerTest {
    private static OvertimeDBManager overtimeDB;

    @BeforeClass
    public static void DBsetUp() {
        overtimeDB = new OvertimeDBManager();
    }

    @Test
    public void setOvertimeTest(){
        boolean res = overtimeDB.setOvertime(2, 53, 760,100);
        boolean expRes = true;

        assertEquals(res, expRes);

        overtimeDB.deleteOvertime(2, 53,760);
    }

    @Test
    public void getOvertimeByUserId(){
        int userId = 3;
        Overtime[] res = overtimeDB.getOvertimeByUserId(userId);
        Overtime[] expRes = {new Overtime(3, 52, 960, 35, false)};

        for(int i = 0; i < res.length; i++){
           assertTrue(expRes[i].equals(res[i]));
        }
    }


    @Test
    public void getUnapprovedOvertimeTest(){
        Overtime[] res = overtimeDB.getUnapprovedOvertime();
        Overtime[] expRes = {
                new Overtime(1,16,960, 60, false),
                new Overtime(1,37,960,-80, false),
                new Overtime(3, 52, 960, 35, false),
                new Overtime(4, 28, 780, -120, false),
                new Overtime(4, 61, 840, -60, false),
                new Overtime(5, 60, 960, 60, false)
        };



        for(int i = 0; i < res.length; i++){
            assertTrue(expRes[i].equals(res[i]));
        }
    }

    // boolean approveOvertime(int userId, int shiftId)
    @Test
    public void approveOvertimeTest(){
        int userId = 10;
        int shiftId = 13;
        int startTime = 960;
        int minutes = 100;

        overtimeDB.setOvertime(userId,shiftId,startTime, minutes);

        boolean res = overtimeDB.approveOvertime(userId, shiftId);
        boolean expRes = true;

        assertEquals(expRes, res);

        overtimeDB.deleteOvertime(userId,shiftId,startTime);
    }

    @Test
    public void deleteOvertimeTest(){

        int userId = 1;
        int shiftId = 29;
        int startTime = 960;
        overtimeDB.setOvertime(userId, shiftId, startTime,30);

        boolean res = overtimeDB.deleteOvertime(userId,shiftId,startTime);
        boolean expRes = true;

        assertEquals(expRes, res);
    }

    @Test
    public void getMinutesTest(){
        int userId = 4;
        String fromDateString = "2017-02-01";
        String toDateString = "2017-02-12";

        Date fromDate = Date.valueOf(fromDateString);
        Date toDate = Date.valueOf(toDateString);


        int res = overtimeDB.getMinutes(userId,fromDate,toDate);
        int expRes = -180;

        assertEquals(expRes, res);
    }
}