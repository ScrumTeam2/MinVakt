package no.ntnu.stud.minvakt.data;

import java.util.ArrayList;

/**
 * Created by evend on 1/11/2017.
 */
public class ShiftUser {
    private int userId;
    private boolean responsibility;
    private boolean valid_absence;
    User user = null;

    public ShiftUser(int userId, boolean responsibility, boolean valid_absence){
        this.userId = userId;
        this.responsibility = responsibility;
        this.valid_absence = valid_absence;
    }

    public ShiftUser() {

    }
    
    public boolean isResponsibility() {
        return responsibility;
    }

    public void setResponsibility(boolean responsibility) {
        this.responsibility = responsibility;
    }

    public boolean isValid_absence() {
        return valid_absence;
    }

    public void setValid_absence(boolean valid_absence) {
        this.valid_absence = valid_absence;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
