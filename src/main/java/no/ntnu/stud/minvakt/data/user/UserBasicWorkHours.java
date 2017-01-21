package no.ntnu.stud.minvakt.data.user;

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

    public UserBasicWorkHours(int id, String firstName, String lastName, User.UserCategory category, int shiftHours, int overTime) {
        super(id, firstName, lastName, category);
        this.shiftHours = shiftHours;
        this.overTime = overTime;
    }

    public UserBasicWorkHours() {
    }

    public static Comparator<UserBasicWorkHours> workHoursComparator = new Comparator<UserBasicWorkHours>() {
        public int compare(UserBasicWorkHours a, UserBasicWorkHours b) {
            return a.getTotalWorkHours() - b.getTotalWorkHours();
        }
    };
}