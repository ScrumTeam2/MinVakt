package no.ntnu.stud.minvakt.data.user;

import no.ntnu.stud.minvakt.data.user.UserBasic;

import java.util.ArrayList;

/**
 * A wrapper class for a list of UserBasic objects. Used for employee search
 */
public class UserBasicList {
    private ArrayList<UserBasic> userBasics;
    private char orderCharacter;

    public UserBasicList(ArrayList<UserBasic> userBasics, char orderCharacter){
        this.userBasics = userBasics;
        this.orderCharacter = orderCharacter;
    }
    public UserBasicList(){}

    public ArrayList<UserBasic> getUserBasics() {
        return userBasics;
    }

    public void setUserBasics(ArrayList<UserBasic> userBasics) {
        this.userBasics = userBasics;
    }

    public char getOrderCharacter() {
        return orderCharacter;
    }

    public void setOrderCharacter(char orderCharacter) {
        this.orderCharacter = orderCharacter;
    }
}
