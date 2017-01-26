package no.ntnu.stud.minvakt.controller.shiftplan;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shiftplan.ShiftPlan;
import no.ntnu.stud.minvakt.data.shiftplan.ShiftPlanDay;
import no.ntnu.stud.minvakt.data.shiftplan.ShiftPlanShift;
import no.ntnu.stud.minvakt.data.shiftplan.ShiftPlanWeek;
import org.junit.Assert;
import org.junit.Test;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Created by Audun on 20.01.2017.
 */
public class ShiftPlanControllerTest {
    @Test
    public void calculateShifPlan() throws Exception {
        final int departmentId = 1;

        ShiftPlanWeek templateWeek = new ShiftPlanWeek();

        templateWeek.addDay(generateTestDay(DayOfWeek.MONDAY, departmentId, 5, 5, 5));
        templateWeek.addDay(generateTestDay(DayOfWeek.TUESDAY, departmentId, 5, 5, 5));
        templateWeek.addDay(generateTestDay(DayOfWeek.WEDNESDAY, departmentId, 5, 5, 5));
        templateWeek.addDay(generateTestDay(DayOfWeek.THURSDAY, departmentId, 5, 5, 5));
        templateWeek.addDay(generateTestDay(DayOfWeek.FRIDAY, departmentId, 5, 5, 5));
        templateWeek.addDay(generateTestDay(DayOfWeek.SATURDAY, departmentId, 10, 10, 10));
        templateWeek.addDay(generateTestDay(DayOfWeek.SUNDAY, departmentId, 5, 5, 5));

        ShiftPlanController controller = new ShiftPlanController(new ShiftPlan(templateWeek, LocalDate.now(), departmentId));
        controller.calculateShifPlan();

        ShiftPlan plan = controller.getShiftPlan();

        for(ShiftPlanWeek week : plan.getGeneratedWeeks()) {
            Assert.assertNotNull(week);

            for(ShiftPlanDay day : week.getDays()) {
                Assert.assertNotNull(day);

                for(ShiftPlanShift shift : day.getShifts()) {
                    Assert.assertNotNull(shift);
                }
            }
        }

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(controller.getShiftPlan());
    }

    private ShiftPlanDay generateTestDay(DayOfWeek dayOfWeek, int departmentId, int employeesDay, int employeesEvening, int employeesNight) {
        ShiftPlanDay day = new ShiftPlanDay(dayOfWeek);
        day.addShift(new ShiftPlanShift(new Shift(-1, employeesDay, Date.valueOf(LocalDate.now()), Shift.ShiftType.DAY, departmentId, new ArrayList<>(), false)));
        day.addShift(new ShiftPlanShift(new Shift(-1, employeesEvening, Date.valueOf(LocalDate.now()), Shift.ShiftType.EVENING, departmentId, new ArrayList<>(), false)));
        day.addShift(new ShiftPlanShift(new Shift(-1, employeesNight, Date.valueOf(LocalDate.now()), Shift.ShiftType.NIGHT, departmentId, new ArrayList<>(), false)));
        return day;
    }

}