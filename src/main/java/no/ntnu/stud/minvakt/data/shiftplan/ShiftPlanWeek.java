package no.ntnu.stud.minvakt.data.shiftplan;

/**
 * Created by Audun on 20.01.2017.
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
