package no.ntnu.stud.minvakt.data;

import java.io.Serializable;

/**
 * Created by Audun on 10.01.2017.
 */
public class User implements Serializable {
    private String name = "Hey";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
