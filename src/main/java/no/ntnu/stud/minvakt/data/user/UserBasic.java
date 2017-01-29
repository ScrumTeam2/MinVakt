package no.ntnu.stud.minvakt.data.user;

import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.util.SanitizeUtil;

/**
 * Represents the user entity. A very basic implementation used when the rest isn't needed
 */

public class UserBasic {
    private int id;
    private String firstName;
    private String lastName;
    private User.UserCategory category;
    public UserBasic() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = SanitizeUtil.filterInput(firstName);
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = SanitizeUtil.filterInput(lastName);
    }


    public User.UserCategory getCategory() {
        return category;
    }

    public void setCategory(User.UserCategory category) {
        this.category = category;
    }

    public UserBasic(int id, String firstName, String lastName, User.UserCategory category) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.category = category;

    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", category=" + category +
                '}';
    }
}
