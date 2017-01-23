package no.ntnu.stud.minvakt.data.shift;

import no.ntnu.stud.minvakt.data.shift.Shift;

import java.util.Date;

/**
 * Created by 8460p on 17.01.2017.
 */
public class ShiftAvailable {
    private int shiftId;
    private Date date;
    private Shift.ShiftType shiftType;
    private String deptName = "";

    public ShiftAvailable(int shiftId, Date date, Shift.ShiftType shiftType, String deptName) {
        this.shiftId = shiftId;
        this.date = date;
        this.shiftType = shiftType;
        this.deptName = deptName;
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
        this.deptName = deptName;
    }
}
