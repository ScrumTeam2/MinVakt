package no.ntnu.stud.minvakt.data;

import java.sql.Date;

/**
 * Created by evend on 1/12/2017.
 */
public class ShiftUserBasic {
    private int shiftId;
    private Date date;
    private Shift.ShiftType shiftType;

    @SuppressWarnings("unused")
    public ShiftUserBasic(){}

    public ShiftUserBasic(int shiftId, Date date, Shift.ShiftType shiftType) {
        this.shiftId = shiftId;
        this.date = date;
        this.shiftType = shiftType;
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
}
