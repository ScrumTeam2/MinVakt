package no.ntnu.stud.minvakt.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.ntnu.stud.minvakt.data.user.User;

import java.io.Serializable;

/**
 * Contains information about an logged in user
 */
public class Session implements Serializable {
    private User user;

    /**
     * Checks if the user is an administrator
     * @return True if the user is an administrator
     */
    public boolean isAdmin() {
        return (user.getCategory().equals(User.UserCategory.ADMIN));
    }

    /**
     * Gets the user for this session
     * @return The user for this session
     */
    public User getUser() {
        return user;
    }

    @JsonIgnore
    public void setUser(User newUser) {
        user = newUser;
    }
}
