package no.ntnu.stud.minvakt.data.shiftplan;

/**
 * Created by Audun on 20.01.2017.
 */
public class ShiftPlanShiftDummy extends ShiftPlanShift {
    @Override
    public boolean requiresMoreNurses() {
        return false;
    }

    @Override
    public boolean requiresMoreHealthWorkers() {
        return false;
    }
}
