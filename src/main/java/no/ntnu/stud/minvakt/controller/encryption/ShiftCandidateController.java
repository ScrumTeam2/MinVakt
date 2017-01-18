package no.ntnu.stud.minvakt.controller.encryption;

import com.sun.jmx.remote.internal.ArrayQueue;
import no.ntnu.stud.minvakt.data.Shift;
import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.data.UserBasicWorkHours;
import no.ntnu.stud.minvakt.util.AvailableUsersUtil;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Stream;

/**
 * Created by Audun on 17.01.2017.
 */
public class ShiftCandidateController {
    private static final double MIN_NURSE_PERCENTAGE = 0.2;
    private static final double MIN_HEALTH_WORKER_PERCENTAGE = 0.3;
    private Shift shift;
    private ArrayList<UserBasicWorkHours> initialCandidates = new ArrayList<>();
    private ArrayList<UserBasicWorkHours> candidates = new ArrayList<>();

    // Internal counters
    private int nurseCount;
    private int healthWorkerCount;

    public ShiftCandidateController(Shift shift) {
        this.shift = shift;
    }

    public ArrayList<UserBasicWorkHours> getPossibleCandidates() {
        AvailableUsersUtil availableUsersUtil = new AvailableUsersUtil();
        initialCandidates = availableUsersUtil.sortAvailableEmployeesIgnoreAvailability(shift.getDate());

        LinkedList<UserBasicWorkHours> initialCandidatesQueue = new LinkedList<>(initialCandidates);
        int nurseGoal = getNurseGoal();
        int healthWorkerGoal = getHealthWorkerGoal();

        // Add all required nurses and health workers
        while(!initialCandidatesQueue.isEmpty()) {
            if (nurseCount >= nurseGoal && healthWorkerCount >= healthWorkerGoal) {
                break;
            }

            UserBasicWorkHours user = initialCandidatesQueue.pop();
            if (nurseCount < nurseGoal && user.getCategory() == User.UserCategory.NURSE) {
                candidates.add(user);
                initialCandidates.remove(user);
                nurseCount++;
            } else if (healthWorkerCount < healthWorkerGoal && user.getCategory() == User.UserCategory.HEALTH_WORKER) {
                candidates.add(user);
                initialCandidates.remove(user);
                healthWorkerCount++;
            }
        }

        if(nurseCount >= nurseGoal && healthWorkerCount >= healthWorkerGoal) {
            // We still need more candidates, and we might have more available
            while (initialCandidates.size() > 0 && candidates.size() < shift.getStaffNumb()) {
                candidates.add(initialCandidates.remove(0));
            }
        }
        return candidates;
    }

    private int getNurseGoal() {
        return  Math.max(1, (int)(shift.getStaffNumb() * MIN_NURSE_PERCENTAGE));
    }

    private int getHealthWorkerGoal() {
        return Math.max(1, (int)(shift.getStaffNumb() * MIN_HEALTH_WORKER_PERCENTAGE));
    }

    private boolean requiresMoreNurses() {
        double percentage = (double)nurseCount / (double)candidates.size();
        return percentage < MIN_NURSE_PERCENTAGE;
    }

    private boolean requiresMoreHealthWorkers() {
        double percentage = (double)healthWorkerCount / (double)candidates.size();
        return percentage < MIN_HEALTH_WORKER_PERCENTAGE;
    }

    //private User getBestNurse() {
      //  Stream.of(initialCandidates).filter()
   // }
}
