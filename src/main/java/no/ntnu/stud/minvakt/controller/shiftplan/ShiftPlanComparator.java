package no.ntnu.stud.minvakt.controller.shiftplan;

import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.shiftplan.ShiftPlanShift;
import no.ntnu.stud.minvakt.data.shiftplan.ShiftPlanUser;

import java.util.Comparator;

/**
 * Created by Audun on 20.01.2017.
 */
public class ShiftPlanComparator implements Comparator<ShiftPlanUser> {
    private static final int DEPARTMENT_WEIGHT = 5;
    private static final int ASSISTANT_WEIGHT = 2;

    private ShiftPlanShift shift;

    public ShiftPlanShift getShift() {
        return shift;
    }

    public void setShift(ShiftPlanShift shift) {
        this.shift = shift;
    }

    public ShiftPlanComparator(ShiftPlanShift shift) {
        this.shift = shift;
    }

    @Override
    public int compare(ShiftPlanUser user1, ShiftPlanUser user2) {
        // Prioritize nurses if they are needed
        if(shift.requiresMoreNurses() && user1.getCategory().equals(User.UserCategory.NURSE)) {
            // We are a nurse, is the other user too?
            if(user2.getCategory().equals(User.UserCategory.NURSE)) {
                return compareShiftUsers(user1, user2); // Prioritize user with least shifts
            } else {
                // The other user is not a nurse, we come first
                return -1;
            }
        }

        // Prioritize health workers if they are needed
        if(shift.requiresMoreHealthWorkers() && user1.getCategory().equals(User.UserCategory.HEALTH_WORKER)) {
            // We are a health worker, is the other user too?
            if(user2.getCategory().equals(User.UserCategory.HEALTH_WORKER)) {
                return compareShiftUsers(user1, user2); // Prioritize user with least shifts
            } else {
                // The other user is not a nurse, we come first
                return -1;
            }
        }
        return compareShiftUsers(user1, user2); // Prioritize user with least shifts
    }

    /**
     * Compares two users. Correct department + least shifts + assistants will be prioritized
     * @param user1 The first user
     * @param user2 The second user
     * @return Negative if the best user is best, 0 if they are equal, positive if the last user is best
     */
    private int compareShiftUsers(ShiftPlanUser user1, ShiftPlanUser user2) {
        int departmentWeight = getDepartmentWeight(user1, user2);
        int categoryWeight = getCategoryWeight(user1, user2);
        int workPercentageWeight = user2.getShiftsNeeded() - user1.getShiftsNeeded();

        return user1.getShiftAmount() - user2.getShiftAmount() + workPercentageWeight + departmentWeight + categoryWeight;
    }

    private int getDepartmentWeight(ShiftPlanUser user1, ShiftPlanUser user2) {
        int departmentWeight = 0;

        // Do not prioritize employees from other departments
        if(user1.getDeptId() == shift.getShift().getDeptId()) {
            departmentWeight -= DEPARTMENT_WEIGHT;
        }
        if(user2.getDeptId() == shift.getShift().getDeptId()) {
            departmentWeight += DEPARTMENT_WEIGHT;
        }
        return departmentWeight;
    }

    private int getCategoryWeight(ShiftPlanUser user1, ShiftPlanUser user2) {
        int weight = 0;

        // Prefer assistants when better roles already are filled
        if(user1.getCategory().equals(User.UserCategory.ASSISTANT)) {
            weight -= ASSISTANT_WEIGHT;
        }

        if(user2.getCategory().equals(User.UserCategory.ASSISTANT)) {
            weight += ASSISTANT_WEIGHT;
        }
        return weight;
    }
}
