package no.ntnu.stud.minvakt.data.shift;

import no.ntnu.stud.minvakt.data.shift.Shift;

import java.sql.Date;

/**
 * Data structure for the many-to-many relationship between shifts and users. Has extra data used for availability
 */
public class ShiftUserAvailability {
    private int shiftId;
    private Date date;
    private Shift.ShiftType shiftType;
    private boolean isAvailable;
    private boolean hasUser;

    public ShiftUserAvailability(int shiftId, Date date, Shift.ShiftType time, boolean isAvailable, boolean hasUser){
        this.shiftId = shiftId;
        this.date = date;
        this.shiftType = time;
        this.isAvailable = isAvailable;
        this.hasUser = hasUser;

    }
    public ShiftUserAvailability(){}

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

    @Override
    public String toString() {
        return "ShiftUserAvailability{" +
                "shiftId=" + shiftId +
                ", date=" + date +
                ", shiftType=" + shiftType +
                ", isAvailable=" + isAvailable +
                ", hasUser=" + hasUser +
                '}';
    }
}
