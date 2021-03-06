package no.ntnu.stud.minvakt.data;

import no.ntnu.stud.minvakt.util.SanitizeUtil;

/**
 * Data structure containing work time for a specific user
 */
public class UserWorkInfo {
    private int userId;
    private String firstName;
    private String lastName;

    private int shiftsWorked;
    private int minutesOvertime;

    public UserWorkInfo(int userId, String firstName, String lastName, int shiftsWorked, int minutesOvertime) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;

        this.shiftsWorked = shiftsWorked;
        this.minutesOvertime = minutesOvertime;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = SanitizeUtil.filterInput(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = SanitizeUtil.filterInput(lastName);
    }

    public int getShiftsWorked() {
        return shiftsWorked;
    }

    public void setShiftsWorked(int shiftsWorked) {
        this.shiftsWorked = shiftsWorked;
    }

    public int getMinutesOvertime() {
        return minutesOvertime;
    }

    public void setMinutesOvertime(int minutesOvertime) {
        this.minutesOvertime = minutesOvertime;
    }

    public int calculateOrdinaryWorkHours() {
        return shiftsWorked * 8;
    }

    public int calculateOvertimeWorkHours() {
        return Math.round(minutesOvertime / 60f);
    }

    public int calculateTotalWorkHours() {
        return calculateOrdinaryWorkHours() + minutesOvertime / 60;
    }

    public int calculateTotalWorkMinutes() {
        return calculateOrdinaryWorkHours() * 60 + minutesOvertime;
    }

    public String getCsvString() {
        return "\n\"" + firstName + "\";\"" + lastName + "\";\"" + calculateOrdinaryWorkHours() + "\";\"" + calculateOvertimeWorkHours() + "\"";
    }
}