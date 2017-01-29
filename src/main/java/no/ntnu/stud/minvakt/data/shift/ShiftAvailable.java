package no.ntnu.stud.minvakt.data.shift;

import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.util.SanitizeUtil;

import java.util.Date;

/**
 * Data structure for the department entity, used for availability
 */
public class ShiftAvailable {
    private int shiftId;
    private Date date;
    private Shift.ShiftType shiftType;
    private String deptName = "";
    private boolean isAvailable;
    private boolean hasUser;

    public ShiftAvailable(int shiftId, Date date, Shift.ShiftType shiftType, String deptName, boolean isAvailable, boolean hasUser) {
        this.shiftId = shiftId;
        this.date = date;
        this.shiftType = shiftType;
        this.deptName = deptName;
        this.isAvailable = isAvailable;
        this.hasUser = hasUser;
    }

    @SuppressWarnings("unused")
    public ShiftAvailable() {
    }

    public int getShiftId() {
        return shiftId;
    }

    public void setShiftId(int shiftId) {
        this.shiftId = shiftId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Shift.ShiftType getShiftType() {
        return shiftType;
    }

    public void setShiftType(Shift.ShiftType shiftType) {
        this.shiftType = shiftType;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = SanitizeUtil.filterInput(deptName);
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isHasUser() {
        return hasUser;
    }

    public void setHasUser(boolean hasUser) {
        this.hasUser = hasUser;
    }

    @Override
    public String toString() {
        return "ShiftUserAvailability{" +
                "shiftId=" + shiftId +
                ", date=" + date +
                ", shiftType=" + shiftType +
                ", DeptName=" + deptName +
                ", isAvailable=" + isAvailable +
                ", hasUser=" + hasUser +
                '}';
    }
}
