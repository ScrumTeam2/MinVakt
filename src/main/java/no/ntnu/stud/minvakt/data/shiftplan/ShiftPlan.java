package no.ntnu.stud.minvakt.data.shiftplan;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Created by Audun on 20.01.2017.
 */
public class ShiftPlan {
    private LocalDate startDate;
    private int departmentId;
    /**
     * The week used as a template for all 6 weeks.
     * Retrieved as a parameter from REST
     */
    @JsonIgnore
    private ShiftPlanWeek templateWeek;

    @JsonIgnore
    private ShiftPlanWeek[] generatedWeeks = new ShiftPlanWeek[6];

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public ShiftPlanWeek getTemplateWeek() {
        return templateWeek;
    }

    public void setTemplateWeek(ShiftPlanWeek templateWeek) {
        if(templateWeek.getDays().length != 7)
            throw new IllegalArgumentException("templateWeek need to have 7 days");

        this.templateWeek = templateWeek;
    }

    @JsonProperty
    public ShiftPlanWeek[] getGeneratedWeeks() {
        return generatedWeeks;
    }

    @JsonIgnore
    public void setGeneratedWeeks(ShiftPlanWeek[] generatedWeeks) {
        this.generatedWeeks = generatedWeeks;
    }

    public ShiftPlan() {
    }

    public ShiftPlan(ShiftPlanWeek templateWeek, LocalDate startDate, int departmentId) {
        this.templateWeek = templateWeek;
        this.startDate = startDate;
        this.departmentId = departmentId;
    }
}
