package no.ntnu.stud.minvakt.controller.shiftplan;

import no.ntnu.stud.minvakt.data.shift.Shift;
import no.ntnu.stud.minvakt.data.shiftplan.*;
import no.ntnu.stud.minvakt.database.ShiftDBManager;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.UserDBManager;

import java.sql.Date;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

/**
 * Contains logic code for generating a shift plan, returning needed data to REST, and saving the generated plan
 */
public class ShiftPlanController {
    private UserDBManager userDBManager = new UserDBManager();
    private ShiftDBManager shiftDBManager = new ShiftDBManager();
    private ShiftPlan shiftPlan;

    /**
     * Keeps track of all users and info used to prioritize them. Used by the priority queues
     */
    private ArrayList<ShiftPlanUser> userList;

    /**
     * Gets the generated shift plan
     * @return The generated shfit plan
     */
    public ShiftPlan getShiftPlan() {
        return shiftPlan;
    }

    /**
     * Initializes a new instance from a shift plan containing a generated week.
     * All normal employees will be loaded from the database in the constructor
     * @param shiftPlan The shift plan
     */
    public ShiftPlanController(ShiftPlan shiftPlan) {
        this.shiftPlan = shiftPlan;
        loadUsers();
    }

    /**
     * Checks if there is any planned shifts in the next 6 weeks (on the given department)
     * @return True if there is no planned shifts
     */
    public boolean verifyValidity() {
        return !shiftDBManager.hasAnyShiftsInPeriod(shiftPlan.getStartDate(), shiftPlan.getStartDate().plusWeeks(6), shiftPlan.getDepartmentId());
    }

    /**
     * Loads all users from the database. Admins are not included
     */
    private void loadUsers() {
        userList = new ArrayList<>();

        ArrayList<User> users = userDBManager.getUsers(false);
        for (User user : users) {
            userList.add(new ShiftPlanUser(user));
        }
    }

    /**
     * Generates the shift plan for the given period. Has to be called before <code>insertShiftsIntoDatabase</code>
     */
    public void calculateShifPlan() {
        ShiftPlanWeek templateWeek = shiftPlan.getTemplateWeek();

        for (int i = 0; i < 6; i++) {
            ShiftPlanWeek week = new ShiftPlanWeek();

            for (int j = 0; j < 7; j++) {
                ShiftPlanDay day = new ShiftPlanDay(DayOfWeek.of(j + 1));

                for (int k = 0; k < 3; k++) {
                    ShiftPlanShift templateShift = templateWeek.getDays()[j].getShifts()[k];
                    ShiftPlanShift shift = new ShiftPlanShift();
                    Shift generatedShift = new Shift(-1, templateShift.getShift().getStaffNumb(), calculateDate(i, j), k, templateShift.getShift().getDeptId(), new ArrayList<>(), false);
                    shift.setShift(generatedShift);
                    day.getShifts()[k] = shift;
                }

                generateCandidates(day); // Magic happens here
                week.getDays()[j] = day;
            }
            shiftPlan.getGeneratedWeeks()[i] = week;
        }
    }

    /**
     * Calculates the date for a week and day relative to the shift plan's start date
     * @param week The relative week. 0 is the same week as the start day
     * @param day The relative day. 0 is the same day of week as the start day
     * @return A Date-object with the calculated date
     */
    private Date calculateDate(int week, int day) {
        LocalDate date = shiftPlan.getStartDate();
        return Date.valueOf(date.plusWeeks(week).plusDays(day));
    }

    /**
     * Generates candidates for a given day
     * @param day The day to get generated candidates for
     */
    private void generateCandidates(ShiftPlanDay day) {
        HashMap<Integer, ShiftPlanUser> usersWorkingToday = new HashMap<>();

        for (ShiftPlanShift shiftPlanShift : day.getShifts()) {
            usersWorkingToday.putAll(shiftPlanShift.generateCandidates(userList));

            // Remove users that has been set up today
            userList.removeAll(usersWorkingToday.values());
        }

        for (ShiftPlanUser user : usersWorkingToday.values()) {
            // Remove comment to ignore users when they've worked their work percentage
            //Add back to list if this user still needs shifts
//            if(user.needsMoreWork()) {
            userList.add(user);
//            }
        }
    }

    /**
     * Adds all the generated shifts into the database, and updates their IDs
     */
    public boolean insertShiftsIntoDatabase() {
        ArrayList<Shift> shifts = new ArrayList<>();
        for(ShiftPlanWeek week : shiftPlan.getGeneratedWeeks()) {
            for(ShiftPlanDay day : week.getDays()) {
                for(ShiftPlanShift shift : day.getShifts()) {
                    shifts.add(shift.getShift());
                }
            }
        }
        return shiftDBManager.bulkInsertShifts(shifts);
    }

    /**
     * Generates a list of all the IDs of the shifts
     * @return An ArrayList containing all the IDs
     */
    public ArrayList<Integer> getShiftIds() {
       ArrayList<Integer> ids = new ArrayList<>();

        for(ShiftPlanWeek week : shiftPlan.getGeneratedWeeks()) {
            for(ShiftPlanDay day : week.getDays()) {
                for(ShiftPlanShift shift : day.getShifts()) {
                    ids.add(shift.getShift().getId());
                }
            }
        }

        return ids;
    }
}
