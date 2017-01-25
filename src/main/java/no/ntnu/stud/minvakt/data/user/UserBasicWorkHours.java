package no.ntnu.stud.minvakt.data.user;

import java.util.Comparator;

/**
 * Created by Marit on 17.01.2017.
 */
public class UserBasicWorkHours extends UserBasic {
    private int shiftMinutes;
    private int overTime;
    private int totalWorkHours;

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

    public int getTotalWorkHours() {
        return totalWorkHours;
    }

    public void setTotalWorkHours(int workHours) {
        this.totalWorkHours = workHours;
    }

    public void calculateTotalWorkHours() {
        totalWorkHours = overTime + shiftMinutes;
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
            return a.getTotalWorkHours() - b.getTotalWorkHours();
        }
    };

    @Override
    public String toString() {
        return "\nUserBasicWorkHours{" +
                super.toString()+
                ", shiftMinutes=" + shiftMinutes +
                ", overTime=" + overTime +
                ", totalWorkHours=" + totalWorkHours +
                "}";
    }
}