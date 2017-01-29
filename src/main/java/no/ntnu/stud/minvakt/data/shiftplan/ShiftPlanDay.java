package no.ntnu.stud.minvakt.data.shiftplan;

import java.time.DayOfWeek;

/**
 * Represents a day in a shift plan
 */
public class ShiftPlanDay {
    private DayOfWeek dayOfWeek;
    private ShiftPlanShift[] shifts;

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * The generated shifts on this day. Length of array is always 3
     */
    public ShiftPlanShift[] getShifts() {
        return shifts;
    }

    public void setShifts(ShiftPlanShift[] shifts) {
        this.shifts = shifts;
    }

    public ShiftPlanDay() {
        shifts = new ShiftPlanShift[3];
    }

    public ShiftPlanDay(DayOfWeek dayOfWeek) {
        this();
        this.dayOfWeek = dayOfWeek;
    }

    public void addShift(ShiftPlanShift shift) {
        shifts[shift.getShift().getType().ordinal()] = shift;
    }
}
