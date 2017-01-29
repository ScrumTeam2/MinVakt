package no.ntnu.stud.minvakt.data;

import java.util.ArrayList;

/**
 * Data structure for a list of shift IDs an user has set itself available for
 */
public class UserAvailableShifts {
    private int userId;
    private ArrayList<Integer> shifts = new ArrayList<>();

    public UserAvailableShifts(){}

    public UserAvailableShifts(int userId, ArrayList<Integer> shifts) {
        this.userId = userId;
        this.shifts = shifts;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public ArrayList<Integer> getShifts() {
        return shifts;
    }

    public void setShifts(ArrayList<Integer> shifts) {
        this.shifts = shifts;
    }
}

