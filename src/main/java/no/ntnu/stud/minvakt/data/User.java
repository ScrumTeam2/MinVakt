package no.ntnu.stud.minvakt.data;

/**
 * Created by evend on 1/10/2017.
 */
public class User {
    private int id;
    private String firstName;
    private String lastName;
    private String hash;
    private String salt;

    public int getId() {
        return id;
    }

    public User() {

    }

    public User(int id, String firstName, String lastName, String hash, String salt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.hash = hash;
        this.salt = salt;
    }
}
