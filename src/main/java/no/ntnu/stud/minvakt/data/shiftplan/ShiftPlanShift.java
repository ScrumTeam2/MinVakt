package no.ntnu.stud.minvakt.data.shiftplan;

import no.ntnu.stud.minvakt.controller.shiftplan.ShiftPlanComparator;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shift.ShiftUser;
import no.ntnu.stud.minvakt.data.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Represents a shift in a shift plan
 */
public class ShiftPlanShift {
    /**
     * The minimum required percentage of nurses
     */
    private static final double MIN_NURSE_PERCENTAGE = 0.2;

    /**
     * The minimum required percentage of health workers
     */
    private static final double MIN_HEALTH_WORKER_PERCENTAGE = 0.3;

    private Shift shift;

    /**
     * Keeps track of all users and the order which they should be added to the shift plan
     */
    private PriorityQueue<ShiftPlanUser> userQueue;

    // Internal counters
    private int nurseCount;
    private int healthWorkerCount;

    private boolean needResponsibilityUser = true;

    /**
     * Gets the underlying Shift object, which contains more info about the shift
     * @return A Shift object
     */
    public Shift getShift() {
        return shift;
    }

    public void setShift(Shift shift) {
        this.shift = shift;
    }

    public ShiftPlanShift() {

    }

    public ShiftPlanShift(Shift shift) {
        this.shift = shift;
    }

    /**
     * Adds user to shift plan
     * First user in queue always gets set with responisibility = true. This user is guaranteed to be a nurse
     * @param user ShiftPlanUser object
     */
    public void addShiftUser(ShiftPlanUser user) {
        shift.getShiftUsers().add(new ShiftUser(user.getId(), user.getFirstName() + " " + user.getLastName(), user.getCategory(), needResponsibilityUser, 0, user.getDeptId()));
        updateCounters(user);
        user.incrementShiftAmount();
        needResponsibilityUser = false;
    }

    /**
     * Updates the nurse/health worker counter after an user has been added
     * @param user
     */
    private void updateCounters(ShiftPlanUser user) {
        if(user.getCategory().equals(User.UserCategory.NURSE))
            nurseCount++;
        else if(user.getCategory().equals(User.UserCategory.HEALTH_WORKER))
            healthWorkerCount++;
    }

    /**
     * @return True if percentage of nurses on a shift is below the requirement
     */
    public boolean requiresMoreNurses() {
        double percentage = (double)nurseCount / (double)shift.getStaffNumb();
        return percentage < MIN_NURSE_PERCENTAGE;
    }

    /**
     * @return True if percentage of health workers on a shift is below the requirement
     */
    public boolean requiresMoreHealthWorkers() {
        double percentage = (double)healthWorkerCount / (double)shift.getStaffNumb();
        return percentage < MIN_HEALTH_WORKER_PERCENTAGE;
    }

    /**
     * Generates candidates for a shift
     * @param userList - ArrayList<ShiftPlanUser>
     * @return HashMap<Integer, ShiftPlanUser>
     */
    public  HashMap<Integer, ShiftPlanUser> generateCandidates(ArrayList<ShiftPlanUser> userList) {
        setUpQueue(userList);

        HashMap<Integer, ShiftPlanUser> usersWorking = new HashMap<>();

        while (shift.getShiftUsers().size() < shift.getStaffNumb() && !userQueue.isEmpty()) {
            ShiftPlanUser user = userQueue.poll();
            if(usersWorking.containsKey(user.getId())) continue;

            addShiftUser(user);
            usersWorking.put(user.getId(), user);
            setUpQueue(userList); // Heavy..
        }

        return usersWorking;
    }

    /**
     * Sets up PriorityQueue for ShiftPlanUser
     * @param userList: ArrayList<ShiftPlanUser>
     */
    private void setUpQueue(ArrayList<ShiftPlanUser> userList) {
        userQueue = new PriorityQueue<>(new ShiftPlanComparator(this));

        for(ShiftPlanUser user : userList) {
            userQueue.add(user);
        }
    }
}
