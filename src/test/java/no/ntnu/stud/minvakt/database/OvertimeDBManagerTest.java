package no.ntnu.stud.minvakt.database;


import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by AnitaKristineAune on 13.01.2017.
 */
public class OvertimeDBManagerTest {
    private static OvertimeDBManager overtimeDB;

    @BeforeClass
    public static void DBsetUp() {
        overtimeDB = new OvertimeDBManager();
    }

    // setOvertime
    // getRowCount
    // getOvertimeList
    // getOvertimeHours

    @Test
    public void setOvertimeTest(){
        // boolean setOvertime(int userId, Date date, int startTime, int endTime)

    }

    @Ignore
  //  @Test
    public void getRowCountTest(){
    // int getRowCount(int userId)
    }
    @Ignore
 //   @Test
    public void getOvertimeList(){
 //       int[][] getOvertimeList(int userId, Date date, int days)
    }
    @Ignore
 //   @Test
    public void getOvertimeHours(){
// int getOvertimeHours(int userId, Date date, int days)
    }


}
