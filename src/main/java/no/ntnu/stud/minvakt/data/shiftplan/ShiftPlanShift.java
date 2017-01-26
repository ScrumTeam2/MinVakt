package no.ntnu.stud.minvakt.data.shiftplan;

import no.ntnu.stud.minvakt.controller.shiftplan.ShiftPlanComparator;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shift.ShiftUser;
import no.ntnu.stud.minvakt.data.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;

/**
 * Created by Audun on 20.01.2017.
 */
public class ShiftPlanShift {
    private static final double MIN_NURSE_PERCENTAGE = 0.2;
    private static final double MIN_HEALTH_WORKER_PERCENTAGE = 0.3;

    private Shift shift;

    /**
     * Keeps track of all users and the order which they should be added to the shift plan
     */
    private PriorityQueue<ShiftPlanUser> userQueue;

    /**
     * The comparator for the priorit queue. Needs to have an updated shift at all times.
     */
    private ShiftPlanComparator comparator;

    // Internal counters
    private int nurseCount;
    private int healthWorkerCount;

    private boolean needResponsibilityUser = true;

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

    public void addShiftUser(ShiftPlanUser user) {
        shift.getShiftUsers().add(new ShiftUser(user.getId(), user.getFirstName() + " " + user.getLastName(), user.getCategory(), needResponsibilityUser, 0, user.getDeptId()));
        updateCounters(user);
        user.incrementShiftAmount();

        // First user in queue always gets set as responisibility user. This user is guaranteed to be a nurse
        needResponsibilityUser = false;
    }

    /**
     * Updates the nurse/healt worker counter after an user has been added
     * @param user
     */
    private void updateCounters(ShiftPlanUser user) {
        if(user.getCategory().equals(User.UserCategory.NURSE))
            nurseCount++;
        else if(user.getCategory().equals(User.UserCategory.HEALTH_WORKER))
            healthWorkerCount++;
    }

    public boolean requiresMoreNurses() {
        double percentage = (double)nurseCount / (double)shift.getStaffNumb();
        return percentage < MIN_NURSE_PERCENTAGE;
    }

    public boolean requiresMoreHealthWorkers() {
        double percentage = (double)healthWorkerCount / (double)shift.getStaffNumb();
        return percentage < MIN_HEALTH_WORKER_PERCENTAGE;
    }

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

    private void setUpQueue(ArrayList<ShiftPlanUser> userList) {
        userQueue = new PriorityQueue<>(new ShiftPlanComparator(this));

        for(ShiftPlanUser user : userList) {
            userQueue.add(user);
        }
    }
}
