package no.ntnu.stud.minvakt.data.department;

/**
 * Data structure for the department entity, used for the calendar
 */
public class DepartmentUser extends Department{
    private boolean hasUser;
    private boolean hasAvailable;

    public DepartmentUser(int id, String name, boolean hasUser, boolean hasAvailable) {
        super(id, name);
        this.hasUser = hasUser;
        this.hasAvailable = hasAvailable;
    }
    public DepartmentUser(){}

    public boolean isHasUser() {
        return hasUser;
    }

    public void setHasUser(boolean hasUser) {
        this.hasUser = hasUser;
    }

    public boolean isHasAvailable() {
        return hasAvailable;
    }

    public void setHasAvailable(boolean hasAvailable) {
        this.hasAvailable = hasAvailable;
    }
}
