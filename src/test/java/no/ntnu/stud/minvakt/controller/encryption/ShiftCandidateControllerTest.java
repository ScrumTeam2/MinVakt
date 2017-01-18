package no.ntnu.stud.minvakt.controller.encryption;

import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.data.UserBasicWorkHours;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Audun on 17.01.2017.
 */
public class ShiftCandidateControllerTest {
    @Test
    public void getPossibleCandidates() throws Exception {
        Shift shift = new ShiftDBManager().getShift(10);
        ShiftCandidateController controller = new ShiftCandidateController(shift);
        ArrayList<UserBasicWorkHours> candidates = controller.getPossibleCandidates();

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