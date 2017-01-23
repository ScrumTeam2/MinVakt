package no.ntnu.stud.minvakt.data;

import java.util.ArrayList;

/**
 * Created by 8460p on 23.01.2017.
 */
public class UserAvailableShifts {
    private int userId;
    private ArrayList<Integer> shifts = new ArrayList<Integer>();

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

