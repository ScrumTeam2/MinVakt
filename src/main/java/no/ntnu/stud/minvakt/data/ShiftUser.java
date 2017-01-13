package no.ntnu.stud.minvakt.data;

import java.util.ArrayList;

/**
 * Created by evend on 1/11/2017.
 */
public class ShiftUser {
    private int userId;
    private String userName;
    private boolean responsibility;
    private boolean valid_absence;
    private User.UserCategory userCategory;
    User user = null;

    public ShiftUser(int userId, String userName, User.UserCategory userCategory, boolean responsibility, boolean valid_absence){
        this.userCategory = userCategory;
        this.userName = userName;
        this.userId = userId;
        this.responsibility = responsibility;
        this.valid_absence = valid_absence;
    }

    public ShiftUser() {

    }
    public User.UserCategory getUserCategory(){
        return userCategory;
    }
    public void setUserCategory(User.UserCategory userCategory){
        this.userCategory = userCategory;
    }
    public String getUserName(){
        return userName;
    }
    public void setUserName(String userName){
        this.userName = userName;
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
