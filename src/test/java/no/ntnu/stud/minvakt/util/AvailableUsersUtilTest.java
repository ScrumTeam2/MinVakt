package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shift.ShiftUser;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasicWorkHours;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Created by Audun on 17.01.2017.
 */
public class AvailableUsersUtilTest {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static AvailableUsersUtil aUU;

    @BeforeClass
    public static void setUpTest(){
         aUU = new AvailableUsersUtil();
    }

    @Test
    public void sortAvailableEmployeesWithCategory() throws Exception {
        LocalDate localDate = LocalDate.parse("2017-02-06", formatter);
        ArrayList<UserBasicWorkHours> sortedUsers = aUU.sortAvailableEmployeesWithCategory(43, localDate, User.UserCategory.ASSISTANT, true);
        //System.out.println(sortedUsers.toString());
        int expResFirst = 4;
        Assert.assertEquals(expResFirst, sortedUsers.get(0).getId());
    }

    @Test
    public void sortAvailableEmployees() throws Exception {
        LocalDate localDate = LocalDate.parse("2017-02-06", formatter);
        ArrayList<UserBasicWorkHours> sortedUsers = aUU.sortAvailableEmployees(43, localDate);
        //System.out.println(sortedUsers.toString());
        int expResFirst = 7;
        Assert.assertEquals(expResFirst, sortedUsers.get(0).getId());
    }


    @Test
    public void sendNotificationOfShiftChangeToValidUsers() throws Exception {
        ShiftDBManager shiftDBM = new ShiftDBManager();
        Timestamp timestamp = Timestamp.from(Instant.now());
        int shiftId = 43;

        Shift shift =shiftDBM.getShift(shiftId);
        ShiftUser shiftUser = shift.getShiftUsers().get(0);
        User user = new User(shiftUser.getUserId(), "Test", "Bruker", "hash", "salt", "email",
                "12345678", User.UserCategory.ASSISTANT,100,1);
        boolean res = aUU.sendNotificationOfShiftChange(shift, user, timestamp);
        boolean expRes = true;
        Assert.assertEquals(expRes, res);
    }

    @Test
    public void sendNotificationToAdminNoAvailableUsers() throws Exception{
        ShiftDBManager shiftDBM = new ShiftDBManager();
        Timestamp timestamp = Timestamp.from(Instant.now());
        int shiftId = 10;

        Shift shift =shiftDBM.getShift(shiftId);
        ShiftUser shiftUser = shift.getShiftUsers().get(0);
        User user = new User(shiftUser.getUserId(), "Stine", "Pettersen", "hash", "salt", "email",
                "12345678", User.UserCategory.ASSISTANT,100,1);
        boolean res = aUU.sendNotificationOfShiftChange(shift, user, timestamp);
        boolean expRes = true;
        Assert.assertEquals(expRes, res);
    }



    /*
    @Test
    public void sortAvailableEmployeesIgnoreAvailability() throws Exception {
        AvailableUsersUtil availableUsersUtil = new AvailableUsersUtil();
        LocalDate localDate = LocalDate.parse("2027-01-01", formatter);
        ArrayList<UserBasicWorkHours> sortedUsers = availableUsersUtil.sortAvailableEmployeesIgnoreAvailability(localDate, 5);
        Assert.assertEquals(5, sortedUsers.size());

    }
    */
}