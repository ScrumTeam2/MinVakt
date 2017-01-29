package no.ntnu.stud.minvakt.data.shiftplan;

/**
 * Reporesents a week in a shift plan
 */
public class ShiftPlanWeek {
    private ShiftPlanDay[] days;

    public ShiftPlanDay[] getDays() {
        return days;
    }

    public void setDays(ShiftPlanDay[] days) {
        this.days = days;
    }

    public void addDay(ShiftPlanDay day) {
        days[day.getDayOfWeek().ordinal()] = day;
    }

    public ShiftPlanWeek() {
        days = new ShiftPlanDay[7];
    }
}
