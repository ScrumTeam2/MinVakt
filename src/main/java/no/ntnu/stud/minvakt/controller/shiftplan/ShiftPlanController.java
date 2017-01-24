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
 * Created by Audun on 17.01.2017.
 */
public class ShiftPlanController {
    private UserDBManager userDBManager = new UserDBManager();
    private ShiftDBManager shiftDBManager = new ShiftDBManager();
    private ShiftPlan shiftPlan;

    /**
     * Keeps track of all users and info used to prioritize them. Used by the priority queues
     */
    private ArrayList<ShiftPlanUser> userList;

    public ShiftPlan getShiftPlan() {
        return shiftPlan;
    }

    public ShiftPlanController(ShiftPlan shiftPlan) {
        this.shiftPlan = shiftPlan;
        loadUsers();
    }

    public boolean verifyValidity() {
        return !shiftDBManager.hasAnyShiftsInPeriod(shiftPlan.getStartDate(), shiftPlan.getStartDate().plusWeeks(6));
    }

    private void loadUsers() {
        userList = new ArrayList<>();

        ArrayList<User> users = userDBManager.getUsers(false);
        for (User user : users) {
            userList.add(new ShiftPlanUser(user));
        }
    }

    public void calculateShifPlan() {
        ShiftPlanWeek templateWeek = shiftPlan.getTemplateWeek();

        for (int i = 0; i < 6; i++) {
            ShiftPlanWeek week = new ShiftPlanWeek();

            for (int j = 0; j < 7; j++) {
                ShiftPlanDay day = new ShiftPlanDay(DayOfWeek.of(j + 1));

                for (int k = 0; k < 3; k++) {
                    ShiftPlanShift templateShift = templateWeek.getDays()[j].getShifts()[k];
                    ShiftPlanShift shift = new ShiftPlanShift();
                    Shift generatedShift = new Shift(-1, templateShift.getShift().getStaffNumb(), calculateDate(i, j), k, templateShift.getShift().getDeptId(), new ArrayList<>());
                    shift.setShift(generatedShift);
                    day.getShifts()[k] = shift;
                }

                generateCandidates(day); // Magic happens here
                week.getDays()[j] = day;
            }
            shiftPlan.getGeneratedWeeks()[i] = week;
        }
    }

    private Date calculateDate(int week, int day) {
        LocalDate date = shiftPlan.getStartDate();
        return Date.valueOf(date.plusWeeks(week).plusDays(day));
    }

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
    public void insertShiftsIntoDatabase() {
       for(ShiftPlanWeek week : shiftPlan.getGeneratedWeeks()) {
           for(ShiftPlanDay day : week.getDays()) {
               for(ShiftPlanShift shift : day.getShifts()) {
                   int shiftId = shiftDBManager.createNewShift(shift.getShift());
                   shift.getShift().setId(shiftId);
               }
           }
       }
    }

    public ArrayList<Integer> getShiftIds() {
       ArrayList<Integer> ids = new ArrayList<>();

        int i = 0;
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
