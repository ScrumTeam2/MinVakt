package no.ntnu.stud.minvakt.database;

import no.ntnu.stud.minvakt.controller.encryption.Encryption;
import no.ntnu.stud.minvakt.controller.encryption.GeneratePassword;
import no.ntnu.stud.minvakt.controller.encryption.MD5Generator;
import no.ntnu.stud.minvakt.data.User;

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
    String sqlLogin = "SELECT * FROM user WHERE email = ? OR phonenumber = ?";
    String sqlChangePass = "UPDATE User SET hash = ?, salt = ? WHERE user_id = ?";
    PreparedStatement prep;
    Connection conn;
    ResultSet res;
    Encryption en = new Encryption();

    /**
     * Tries to log in an user with either mail or phone number
     * @param email The mail or phone number
     * @param pass The password, plaintext
     * @return Returns an user object if the login is correct
     */
    public User loginUser(String email, String pass) {
        if (setUp()) {
            try {
                // startTransaction(); //Trengs denne?
                prep = getConnection().prepareStatement(sqlLogin);
                prep.setString(1, email);
                prep.setString(2, email);
                res = prep.executeQuery();
                if (res.next()) {
                    if (en.passDecoding(pass, res.getString("hash"), res.getString("salt"))) {
                        //New user
                        //User user = new User(res.getInt("user_id"), res.getString("first_name"), res.getString("last_name"), res.getString("email"), res.getString("phonenumber"), res.getInt("rights"), res.getInt("category"), res.getInt("percentage_work");
                        User user = new User(res.getInt("user_id"), res.getString("first_name"), res.getString("last_name"), null, null);
                        return user;
                    }
                }

            } catch (Exception e) {
                System.out.println("Error at loginObj");
                e.printStackTrace();
            }
        }
        return null;
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

    //Returnerer int 1 dersom bruker har blitt opprettet. Kan ogsÃ¥ endres til Ã¥ returnere objekt med brukernavn, passord, email og phone (for Ã¥ da sende email til brukeren med brukerdata)
    public int createNewAnsatt(String first_name, String last_name, String email, String phone, String category) { //AnsattNr? , Navn = etternavn, fornavn mellomnavn. Parse etternavn (fÃ¸r komma)
        int creation = -1;
        String sqlInsert = "INSERT INTO User (first_name, last_name, hash, salt, email, phonenumber) VALUES (?,?,?,?,?,?)";
        String randomPass = GeneratePassword.generateRandomPass();
        //sendEmailWithGeneratedPass to registered user in this method
        String hashedPass[] = en.passEncoding(randomPass);
        String salt = hashedPass[0];
        String hash = hashedPass[1];
        if (setUp()) {
            try {
                startTransaction();
                prep.setString(1, first_name);
                prep.setString(2, last_name);
                prep.setString(3, hash);
                prep.setString(4, salt);
                prep.setString(5, email);
                prep.setString(6, phone);
                prep = getConnection().prepareStatement(sqlInsert);
                creation = prep.executeUpdate();
            } catch (Exception e) {
                System.out.println("Issue creating new ansatt");
                e.printStackTrace();
            } finally {
                endTransaction();
                finallyStatement(prep);
            }
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
