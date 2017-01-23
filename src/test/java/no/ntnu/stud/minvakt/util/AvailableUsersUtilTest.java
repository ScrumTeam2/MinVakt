package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.user.UserBasicWorkHours;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Created by Audun on 17.01.2017.
 */
public class AvailableUsersUtilTest {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Test
    public void sortAvailableEmployeesIgnoreAvailability() throws Exception {
        AvailableUsersUtil availableUsersUtil = new AvailableUsersUtil();
        LocalDate localDate = LocalDate.parse("2027-01-01", formatter);
        ArrayList<UserBasicWorkHours> sortedUsers = availableUsersUtil.sortAvailableEmployeesIgnoreAvailability(localDate, 5);
        Assert.assertEquals(5, sortedUsers.size());
    }

    @Test
    public void sortAvailableEmployees() throws Exception {
        AvailableUsersUtil availableUsersUtil = new AvailableUsersUtil();
        LocalDate localDate = LocalDate.parse("2017-02-06", formatter);
        ArrayList<UserBasicWorkHours> sortedUsers = availableUsersUtil.sortAvailableEmployees(43, localDate);
        //System.out.println(sortedUsers.toString());
        int expResFrist = 7;
        Assert.assertEquals(expResFrist, sortedUsers.get(0).getId());

    }

}