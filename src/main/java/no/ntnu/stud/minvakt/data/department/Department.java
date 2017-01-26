package no.ntnu.stud.minvakt.data.department;

import no.ntnu.stud.minvakt.services.SecureService;

/**
 * Created by evend on 1/10/2017.
 */
public class Department {
    private int id;
    private String name;

    public Department(){}
    public Department(int id, String name){
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
