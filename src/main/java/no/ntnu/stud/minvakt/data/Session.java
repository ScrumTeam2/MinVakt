package no.ntnu.stud.minvakt.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by Audun on 10.01.2017.
 */
public class Session implements Serializable {
    private User user;

    public boolean isAdmin() {
        return true; // TODO
    }

    public User getUser() {
        return user;
    }

    @JsonIgnore
    public void setUser(User newUser) {
        user = newUser;
    }
}
