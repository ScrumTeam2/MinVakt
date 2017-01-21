package no.ntnu.stud.minvakt.data.shiftplan;

import java.time.DayOfWeek;

/**
 * Created by Audun on 20.01.2017.
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
