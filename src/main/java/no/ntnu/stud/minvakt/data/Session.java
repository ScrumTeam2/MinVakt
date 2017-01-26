package no.ntnu.stud.minvakt.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import no.ntnu.stud.minvakt.data.user.User;

import java.io.Serializable;

/**
 * Created by Audun on 10.01.2017.
 */
public class Session implements Serializable {
    private User user;

    public boolean isAdmin() {
        return (user.getCategory().equals(User.UserCategory.ADMIN));
    }

    public User getUser() {
        return user;
    }

    @JsonIgnore
    public void setUser(User newUser) {
        user = newUser;
    }
}
