package no.ntnu.stud.minvakt.data;

import java.util.Comparator;

/**
 * Created by Marit on 17.01.2017.
 */
public class UserBasicWorkHours extends UserBasic {
    private int shiftHours;
    private int overTime;
    private int totalWorkHours;

    public int getShiftHours() {
        return shiftHours;
    }

    public void setShiftHours(int shiftHours) {
        this.shiftHours = shiftHours;
    }

    public int getOverTime() {
        return overTime;
    }

    public void setOvertime(int overTime) {
        this.overTime = overTime;
    }

    public int getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(int workHours) {
        this.totalWorkHours = workHours;
    }

    public void calculateTotalWorkHours() {
        totalWorkHours = overTime + shiftHours;
    }

    public static Comparator<UserBasicWorkHours> workhoursComparator = new Comparator<UserBasicWorkHours>() {
        public int compare(UserBasicWorkHours a, UserBasicWorkHours b) {
            return a.getTotalWorkHours() - b.getTotalWorkHours();
        }
    };
}