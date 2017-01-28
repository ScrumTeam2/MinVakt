package no.ntnu.stud.minvakt.data.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.ntnu.stud.minvakt.util.SanitizeUtil;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.owasp.html.Sanitizers;

import java.lang.invoke.MethodHandles;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public enum UserCategory {
        ADMIN, ASSISTANT, HEALTH_WORKER, NURSE;



        public int getValue() {
            return super.ordinal();
        }

        public static UserCategory valueOf(int shiftTypeNr) {
            for (UserCategory type : UserCategory.values()) {
                if (type.ordinal() == shiftTypeNr) {
                    return type;
                }
            }
            return null;
        }

        @SuppressWarnings("unused")
        public static UserCategory fromString(String shiftTypeNr) {
            try {
                int val = Integer.parseInt(shiftTypeNr);
                return valueOf(val);
            } catch (NumberFormatException e) {
                log.log(Level.WARNING, "Invalid UserCategory " + shiftTypeNr, e);
                return UserCategory.HEALTH_WORKER;
            }
        }

        @Override
        public String toString() {
            String constName = super.toString();
            return constName.substring(0, 1) + constName.substring(1).toLowerCase();
        }

    }
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserCategory category;
    private float workPercentage;
    private int deptId;

    @JsonIgnore
    private String hash;

    @JsonIgnore
    private String salt;

    public User() {

    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
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

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = SanitizeUtil.filterInput(email);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = SanitizeUtil.filterInput(phoneNumber);
    }

    public UserCategory getCategory() {
        return category;
    }

    public void setCategory(UserCategory category) {
        this.category = category;
    }

    public float getWorkPercentage() {
        return workPercentage;
    }

    public void setWorkPercentage(float workPercentage) {
        this.workPercentage = workPercentage;
    }

    public User(int id, String firstName, String lastName, String hash, String salt, String email, String phoneNumber, UserCategory userCategory, float workPercentage,
                int deptId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hash = hash;
        this.salt = salt;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.category = userCategory;
        this.workPercentage = workPercentage;
        this.deptId = deptId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", category=" + category +
                ", workPercentage=" + workPercentage +
                ", deptId=" + deptId +
                ", hash='" + hash + '\'' +
                ", salt='" + salt + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
