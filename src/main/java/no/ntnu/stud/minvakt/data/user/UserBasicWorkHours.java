package no.ntnu.stud.minvakt.data.user;

import java.util.Comparator;

/**
 * Represents the user entity, with additional info about work hours
 */
public class UserBasicWorkHours extends UserBasic {
    private int shiftMinutes;
    private int overTime;
    private int totalWorkMinutes;

    public int getShiftMinutes() {
        return shiftMinutes;
    }

    public void setShiftMinutes(int shiftMinutes) {
        this.shiftMinutes = shiftMinutes;
    }

    public int getOverTime() {
        return overTime;
    }

    public void setOvertime(int overTime) {
        this.overTime = overTime;
    }

    public int getTotalWorkMinutes() {
        return totalWorkMinutes;
    }

    public void setTotalWorkMinutes(int workHours) {
        this.totalWorkMinutes = workHours;
    }

    public void calculateTotalWorkHours() {
        totalWorkMinutes = overTime + shiftMinutes;
    }

    public UserBasicWorkHours(int id, String firstName, String lastName, User.UserCategory category, int shiftMinutes, int overTime) {
        super(id, firstName, lastName, category);
        this.shiftMinutes = shiftMinutes;
        this.overTime = overTime;
    }

    public UserBasicWorkHours() {
    }

    public static Comparator<UserBasicWorkHours> workHoursComparator = new Comparator<UserBasicWorkHours>() {
        public int compare(UserBasicWorkHours a, UserBasicWorkHours b) {
            return a.getTotalWorkMinutes() - b.getTotalWorkMinutes();
        }
    };

    @Override
    public String toString() {
        return "\nUserBasicWorkHours{" +
                super.toString()+
                ", shiftMinutes=" + shiftMinutes +
                ", overTime=" + overTime +
                ", totalWorkMinutes=" + totalWorkMinutes +
                "}";
    }
}