package no.ntnu.stud.minvakt.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.lang.invoke.MethodHandles;
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

    @JsonIgnore
    private String hash;

    @JsonIgnore
    private String salt;

    public User() {

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
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public UserCategory getCategory() {
        return category;
    }

    public void setCategory(UserCategory category) {
        this.category = category;
    }

    public User(int id, String firstName, String lastName, String hash, String salt, UserCategory userCategory) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hash = hash;
        this.salt = salt;
        this.category = userCategory;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", hash='" + hash + '\'' +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", category=" + category +
                '}';
    }
}
