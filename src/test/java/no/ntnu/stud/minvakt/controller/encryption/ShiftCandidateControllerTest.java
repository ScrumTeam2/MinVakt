package no.ntnu.stud.minvakt.controller.encryption;

import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.ShiftUser;
import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.data.UserBasicWorkHours;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Audun on 17.01.2017.
 */
public class ShiftCandidateControllerTest {
    @Test
    public void getPossibleCandidates() throws Exception {
        ShiftDBManager shiftDBManager = new ShiftDBManager();
        int shiftId = shiftDBManager.createNewShift(new Shift(-1, 5, Date.valueOf(LocalDate.now()), 1, 1, new ArrayList<ShiftUser>()));
        Shift shift = shiftDBManager.getShift(shiftId);

        ShiftCandidateController controller = new ShiftCandidateController(shift);
        ArrayList<UserBasicWorkHours> candidates = controller.getPossibleCandidates();
        controller.savePossibleCandidates();

        // Retrieve shift with candidates, check them
        Shift retrievedShift = shiftDBManager.getShift(shiftId);
        Assert.assertEquals(candidates.size(), retrievedShift.getShiftUsers().size());

        shiftDBManager.deleteShift(shiftId);

        int lastWorkHours = -1;
        User.UserCategory lastCategory = User.UserCategory.NURSE;

        // Loop through all the candidates, and check that they have increasing work hours within their category
        for(UserBasicWorkHours user : candidates) {
            if(user.getCategory().equals(lastCategory)) {
                Assert.assertTrue(user.getTotalWorkHours() >= lastWorkHours);
                lastWorkHours = user.getTotalWorkHours();
            } else {
                lastWorkHours = -1;
                lastCategory = user.getCategory();
            }
        }
    }

}