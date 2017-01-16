package no.ntnu.stud.minvakt.database;
import no.ntnu.stud.minvakt.data.Overtime;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Ignore
    public void setOvertimeTest(){
        Date date = new Date(System.currentTimeMillis());

        boolean res = overtimeDB.setOvertime(14, date, 20,25);
        boolean expRes = true;

        assertEquals(res, expRes);
    }

    @Test
    public void getRowCountTest(){
        String stringDate = "2017-01-01";
        String stringDate2 = "2017-01-05";
        Date date = Date.valueOf(stringDate);
        Date date2 = Date.valueOf(stringDate2);

        int res = overtimeDB.getRowCount(1, date, date2);
        int expRes = 4;
        assertEquals(res, expRes);
    }

    @Test
    public void getOvertimeListTest(){
        String stringDate = "2017-01-01";
        String stringDate2 = "2017-01-02";
        Date date = Date.valueOf(stringDate);
        Date date2 = Date.valueOf(stringDate2);

        Overtime data1 = new Overtime(date,24,26);
        Overtime data2 = new Overtime(date2, 86,88);

        Overtime[] res = overtimeDB.getOvertimeList(1, date, date2);
        Overtime[] expRes = {data1, data2};

        for(int i = 0; i < expRes.length; i++){
            assertTrue(res[i].equals(expRes[i]));
        }
    }

    @Test
    public void getOvertimeHours(){
        String stringDate = "2017-01-01";
        String stringDate2 = "2017-01-05";
        Date date = Date.valueOf(stringDate);
        Date date2 = Date.valueOf(stringDate2);

        int res = overtimeDB.getOvertimeHours(1, date, date2);
        int expRes = 16;

        assertEquals(expRes,res);
    }
}