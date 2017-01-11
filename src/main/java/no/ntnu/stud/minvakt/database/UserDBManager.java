package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.controller.encryption.Encryption;
import no.ntnu.stud.minvakt.controller.encryption.GeneratePassword;
import no.ntnu.stud.minvakt.controller.encryption.MD5Generator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author 8460p
 */
public class UserDBManager extends DBManager {
    //If string contains @, it's an email
   /* if(username.contains("@")) {
     checkLogin(username, password); //Email
     } else {
     checkLogin(username, password); //Phone
     }*/

    String sqlLoginId = "SELECT * FROM User where user_id=?";
    String sqlLoginEmail = "SELECT * FROM User where email=?";
    String sqlLoginPhone = "SELECT * FROM User where phone=?";
    String sqlChangePass = "UPDATE User SET hash = ?, salt = ? WHERE user_id = ?";
    PreparedStatement prep;
    Connection conn;
    ResultSet res;
    Encryption en = new Encryption();

    //@Deprecated. Use loginObjEmail instead
    public int checkLoginEmail(String email, String pass) {
        int login = -1;

        /*String[] passInfo = en.passEncoding(pass);
         String pw = passInfo[0];
         string salt = passInfo[1];
         */
//        String hashedPass = MD5Generator.md5generate(pass);
        if (setUp()) {
            try {
                startTransaction();
                prep = getConnection().prepareStatement(sqlLoginEmail);
                prep.setString(1, email);
                res = prep.executeQuery();
                if (res.next()) {
                    if (en.passDecoding(pass, res.getString("hash"), res.getString("salt"))) {
                        res.getInt(1);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return login;
    }

    //Returns an object with user data if the login is correct
    public Object loginObjEmail(String email, String pass) {
        Object[] userData = new Object[7];
        if (setUp()) {
            try {
                // startTransaction(); //Trengs denne?
                prep = getConnection().prepareStatement(sqlLoginEmail);
                prep.setString(1, email);
                res = prep.executeQuery();
                if (res.next()) {
                    if (en.passDecoding(pass, res.getString("hash"), res.getString("salt"))) {
                        //New user
                        return new Object[]{res.getInt("user_id"), res.getString("first_name"), res.getString("last_name"), res.getString("email"), res.getString("phonenumber"), res.getInt("rights"), res.getInt("category"), res.getInt("percentage_work")};
                    }
                }

            } catch (Exception e) {
                System.out.println("Error at loginObj");
                e.printStackTrace();
            }
        }
        return userData;
    }

    public Object loginObjPhone(String phone, String pass) {
        Object[] userData = new Object[7];
        if (setUp()) {
            try {
                // startTransaction(); //Trengs denne?
                prep = getConnection().prepareStatement(sqlLoginPhone);
                prep.setString(1, phone);
                res = prep.executeQuery();
                if (res.next()) {
                    if (en.passDecoding(pass, res.getString("hash"), res.getString("salt"))) {
                        return new Object[]{res.getInt("user_id"), res.getString("first_name"), res.getString("last_name"), res.getString("email"), res.getString("phonenumber"), res.getInt("rights"), res.getInt("category"), res.getInt("percentage_work")};
                    }
                }

            } catch (Exception e) {
                System.out.println("Error at loginObj");
                e.printStackTrace();
            }
        }
        return userData;
    }

    //@Deprecated. Use loginObjPhone() instead
    public int checkLoginPhone(String phone, String pass) {
        int login = -1;
        String sqlLogin = "SELECT * FROM User where phonenumber=? and pass=?";
        String hashedPass = MD5Generator.md5generate(pass);
        if (setUp()) {
            try {
                startTransaction();
                prep = getConnection().prepareStatement(sqlLogin);
                prep.setString(1, phone);
                prep.setString(2, hashedPass);
                ResultSet res = prep.executeQuery();
                if (res.next()) {
                    login = res.getInt(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return login;
    }

    //Check login via user ID. used when changing user's password
    public int checkLoginId(String userId, String pass) {
        int login = -1;
        if (setUp()) {
            try {
                startTransaction();
                prep = getConnection().prepareStatement(sqlLoginId);
                prep.setString(1, userId);
                res = prep.executeQuery();
                if (res.next()) {
                    if (en.passDecoding(pass, res.getString("hash"), res.getString("salt"))) {
                        login = res.getInt(1);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return login;
    }

    public int deleteUser(String user_id) {
        return -1;
        //"Deletes" the row of this specific user? Possible?
    }

    public int changeDepartment(String user_id) {
        int change = -1;
        String sqlChangeDep = "UPDATE dept_id FROM User where user_id=?";
        if (setUp()) {
            try {
                startTransaction();
                prep = getConnection().prepareStatement(sqlChangeDep);
                prep.executeUpdate();
                change = 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return change;
    }

    public int createNewAnsatt(String first_name, String last_name, String email, String phone, String category) { //AnsattNr? , Navn = etternavn, fornavn mellomnavn. Parse etternavn (fÃ¸r komma)
        int creation = -1;
        String sqlInsert = "INSERT INTO User (first_name, last_name, hash, salt, email, phonenumber) VALUES (?,?,?,?,?,?)";
        String randomPass = GeneratePassword.generateRandomPass();
        String hashedPass = MD5Generator.md5generate(randomPass);
        String salt = "getSaltHere";
        //Get hash
        //Get salt

        try {
            prep.setString(1, first_name);
            prep.setString(2, last_name);
            prep.setString(3, randomPass); //Hash
            //SaltHere
            prep.setString(4, salt);
            prep.setString(5, email);
            prep.setString(6, phone);
            startTransaction();
            prep = getConnection().prepareStatement(sqlInsert);
            prep.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return creation;
    }

    public int changePasswordUserId(String user_id, String prev_password, String new_password) {
        int change = -1;
        String sqlChangePass = "UPDATE User SET hash = ?, salt = ? WHERE user_id = ?";
        boolean oldPassCorrect = false;
        try {
            prep = getConnection().prepareStatement(sqlLoginId);
            prep.setString(1, user_id);
            res = prep.executeQuery();
            if (res.next()) {
                if (en.passDecoding(prev_password, res.getString("hash"), res.getString("salt"))) {
                    oldPassCorrect = true;
                }
            }
        } catch (Exception e) {
            System.out.println("Error at changePasswordUserId()");
            e.printStackTrace();
        }
        if(oldPassCorrect) {
            //Change Pass
            String[] passInfoNew = en.passEncoding(prev_password);
            String hashNew = passInfoNew[0];
            String saltNew = passInfoNew[1];
            try {
                prep = getConnection().prepareStatement(sqlChangePass);
                prep.setString(1, hashNew);
                prep.setString(1, saltNew);
                prep.setString(1, user_id);
                change = prep.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error at changePasswordUserId() update");
                e.printStackTrace();
            }
        } else {
            System.out.println("Login incorrect");
        }
        return change;
    }

}
