package no.ntnu.stud.minvakt.data.shift;

import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasicWorkHours;
import no.ntnu.stud.minvakt.util.SanitizeUtil;

/**
 * Created by evend on 1/11/2017.
 */
public class ShiftUser {
    private int userId;
    private String userName;
    private boolean responsibility;
    private int valid_absence;
    private User.UserCategory userCategory;
    User user = null;
    private boolean shift_change;
    private boolean removed;
    private int departmentId;

    public ShiftUser(int userId, String userName, User.UserCategory userCategory, boolean responsibility, int valid_absence, int departmentId){
        this.userCategory = userCategory;
        this.userName = userName;
        this.userId = userId;
        this.responsibility = responsibility;
        this.valid_absence = valid_absence;
        this.shift_change = false;
        this.removed = false;
        this.departmentId = departmentId;
    }

    public ShiftUser(int userId, String userName, User.UserCategory userCategory, boolean responsibility, int valid_absence, boolean shift_change, boolean removed,int departmentId){
        this.userCategory = userCategory;
        this.userName = userName;
        this.userId = userId;
        this.responsibility = responsibility;
        this.valid_absence = valid_absence;
        this.shift_change = shift_change;
        this.removed = removed;
        this.departmentId = departmentId;
    }
    @SuppressWarnings("unused")
    public ShiftUser() {

    }

    public ShiftUser(UserBasicWorkHours user) {
        this.userId = user.getId();
        this.userName = user.getFirstName() + " " + user.getLastName();
        this.responsibility = false;
        this.valid_absence = 0;
        this.userCategory = user.getCategory();
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
        this.userName = SanitizeUtil.filterInput(userName);
    }
    public boolean isResponsibility() {
        return responsibility;
    }

    public void setResponsibility(boolean responsibility) {
        this.responsibility = responsibility;
    }

    public boolean isValid_absence() {
        if(valid_absence>0) {
            return true;
        }
        return false;
    }

    public int getValidAbsence() {
        return valid_absence;
    }
    public int getValid_absence() {
        return valid_absence;
    }

    public void setValid_absence(int valid_absence) {
        this.valid_absence = valid_absence;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isShift_change() {
        return shift_change;
    }

    public void setShift_change(boolean shift_change) {
        this.shift_change = shift_change;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }
    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    @Override
    public String toString() {
        return "ShiftUser{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", departmentId=" + departmentId +
                ", userCategory=" + userCategory +
                ", responsibility=" + responsibility +
                '}';
    }
}
