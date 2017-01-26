package no.ntnu.stud.minvakt.data.shiftplan;

import no.ntnu.stud.minvakt.data.user.User;

/**
 * Created by Audun on 20.01.2017.
 */
public class ShiftPlanUser extends User {
    private static final int SHIFT_AMOUNT_100_PERCENT = 30;
    /**
     * The amount of generated shifts the user already has been added to
     */
    private int shiftAmount;

    public int getShiftAmount() {
        return shiftAmount;
    }

    public void incrementShiftAmount() {
        shiftAmount++;
    }

    public boolean needsMoreWork() {
        return shiftAmount < getShiftsNeeded();
    }

    public int getShiftsNeeded() {
        return (int)(getWorkPercentage() * SHIFT_AMOUNT_100_PERCENT);
    }

    public ShiftPlanUser(int id, String firstName, String lastName, String hash, String salt, String email, String phoneNumber, UserCategory userCategory, float percentageWork, int deptId) {
        super(id, firstName, lastName, hash, salt, email, phoneNumber, userCategory, percentageWork, deptId);
    }

    public ShiftPlanUser(User user) {
        this(user.getId(), user.getFirstName(), user.getLastName(), user.getHash(), user.getSalt(), user.getEmail(), user.getPhoneNumber(), user.getCategory(), user.getWorkPercentage(),user.getDeptId());
    }
}
