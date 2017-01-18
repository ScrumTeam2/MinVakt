package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.UserBasicWorkHours;
import org.junit.Test;

import java.sql.Date;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Audun on 17.01.2017.
 */
public class AvailableUsersUtilTest {
    @Test
    public void sortAvailableEmployeesIgnoreAvailability() throws Exception {
        AvailableUsersUtil availableUsersUtil = new AvailableUsersUtil();
        ArrayList<UserBasicWorkHours> sortedUsers = availableUsersUtil.sortAvailableEmployeesIgnoreAvailability(Date.valueOf("2017-01-13"));
    }

    @Test
    public void sortAvailableEmployees() throws Exception {
        AvailableUsersUtil availableUsersUtil = new AvailableUsersUtil();
        ArrayList<UserBasicWorkHours> sortedUsers = availableUsersUtil.sortAvailableEmployees(1, Date.valueOf("2017-01-11"));
    }

}