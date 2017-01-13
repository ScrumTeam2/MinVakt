package no.ntnu.stud.minvakt.data;

public class UserBasic {
    private int id;
    private String firstName;
    private String lastName;
    private String category;
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
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public UserBasic(int id, String firstName, String lastName, int category) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        if(category ==1) {
            this.category = "Assistent";
        } else if(category ==2) {
            this.category = "Helsefagarbeider";
        } else if(category ==3) {
            this.category = "Sykepleier";
        }
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
