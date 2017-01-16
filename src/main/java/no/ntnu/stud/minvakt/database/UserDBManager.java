package no.ntnu.stud.minvakt.database;

import com.mysql.cj.api.jdbc.Statement;
import no.ntnu.stud.minvakt.controller.encryption.Encryption;
import no.ntnu.stud.minvakt.controller.encryption.GeneratePassword;
import no.ntnu.stud.minvakt.data.User;
import no.ntnu.stud.minvakt.data.UserBasic;
import no.ntnu.stud.minvakt.util.QueryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

import no.ntnu.stud.minvakt.data.UserBasic;

public class UserDBManager extends DBManager {
    public UserDBManager() {
        super();
    }
    private final String sqlLoginId = "SELECT * FROM user where user_id=?;";
    private final String sqlLogin = "SELECT * FROM user WHERE email = ? OR phonenumber = ?;";
    private final String sqlChangePass = "UPDATE user SET hash = ?, salt = ? WHERE user_id = ?;";
    private final String sqlGetUsers = "SELECT * FROM user;";
    private final String sqlGetUserById = "SELECT * FROM user WHERE user_id = ?;";
    private final String sqlCreateNewUser = "INSERT INTO user (first_name, last_name, hash, salt, email, phonenumber, category) VALUES (?,?,?,?,?,?,?);";
    private final String sqlChangeUserInfo = "UPDATE user SET first_name = ?, last_name = ?, email =?, phonenumber =? WHERE user_id =?;";
    private final String sqlIsAdmin = "SELECT * FROM admin WHERE user_id = ?";
    private final String sqlGetUserBasics = "SELECT user_id, first_name, last_name, category FROM user;";
    private final String sqlChangeDep = "UPDATE dept_id FROM user where user_id=?";
    private final String sqlDeleteUser = "DELETE FROM user WHERE user_id = ?";


    //If string contains @, it's an email
   /* if(username.contains("@")) {
     checkLogin(username, password); //Email
     } else {
     checkLogin(username, password); //Phone
     }*/

    PreparedStatement prep;
    Connection conn;
    ResultSet res;
    Encryption en = new Encryption();
    
     /*Contents
        loginUser()
        checkLoginId()
        getUsers();
        getUserById();
        createNewUser();
        changePasswordUserId();
        changeDepartment();
    */
    
    /**
     * Tries to log in a user with either mail or phone number
     * @param username The mail or phone number
     * @param pass The password, plaintext
     * @return Returns a user object if the login is correct
     */
    public User loginUser(String username, String pass) {
        if (setUp()) {
            try {
                startTransaction();
                prep = getConnection().prepareStatement(sqlLogin);
                prep.setString(1, username);
                prep.setString(2, pass);
                res = prep.executeQuery();
                if (res.next()) {
                    System.out.println(username+ " " + pass);

                    if (en.passDecoding(pass, res.getString("hash"), res.getString("salt"))) {
                        //New user
                        //User user = new User(res.getInt("user_id"), res.getString("first_name"), res.getString("last_name"), res.getString("email"), res.getString("phonenumber"), res.getInt("rights"), res.getInt("category"), res.getInt("percentage_work");
                        User user = new User(res.getInt("user_id"), res.getString("first_name"),
                                res.getString("last_name"), null,
                                null, User.UserCategory.valueOf(res.getInt("category")));
                        return user;
                    }
                    else{
                        throw new Exception("User with username = "+username+" typed incorrect password!"+pass);
                    }
                }

            } catch (Exception e) {
                System.out.println("Error at loginUser");
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Checks whether the user ID and password matches a row in the database
     * @param userId
     * @param Password plaintext
     * @return Integer>-1 if success, -1 if fail
     */
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

    public int checkUserAdmin(String userId) {
        int isAdmin = -1;
        return isAdmin;
    }

    public boolean deleteUser(int userId) {
        //"Deletes" the row of this specific user? Possible?
        if (setUp()) {
            try {
                prep = getConnection().prepareStatement(sqlDeleteUser);
                prep.setInt(1, userId);

                int affectedRows = prep.executeUpdate();
                if(affectedRows == 0) {
                    log.info("userId " + userId + " not found when deleting");
                    return false;
                }

                return true;
            } catch (Exception e) {
                log.log(Level.SEVERE, "Failed to delete user with userId " + userId, e);
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return false;
    }


    public int changeDepartment(int user_id) {
        int change = -1;
        if (setUp()) {
            try {
                prep = getConnection().prepareStatement(sqlChangeDep);
                prep.setInt(1,user_id);
                change = prep.executeUpdate();

            } catch (Exception e) {
                System.err.println("Isse with changing department for userId = "+user_id);
                e.printStackTrace();
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return change;
    }
    
     /**
     * Returns an array with user objects.
     * @return User object
     */

    
     public ArrayList<UserBasic> getUsersBasics(){
        ArrayList<UserBasic> users = new ArrayList<UserBasic>();
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetUsers);
                res = prep.executeQuery();
                while (res.next()){
                    int userId = res.getInt("user_id");
                    String firstName =res.getString("first_name");
                    String lastName = res.getString("last_name");
                    int category = res.getInt("category");
                    UserBasic user = new UserBasic(userId,firstName,lastName,
                            User.UserCategory.valueOf(category));
                    users.add(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return users;
    }
    
    /* //Deprecated
    public String[][] getTableAllUsers(){
        String[] tupleArray = {"user_id", "first_name", "last_name", "email", "phonenumber"};
        String sqlUsers = "Select (user_id, first_name, last_name, email, phonenumber) FROM User";

        ArrayList<ArrayList<String>> users= new ArrayList<>();
        if (setUp()) {
            try {
                startTransaction();
                prep = getConnection().prepareStatement(sqlLogin);
                ResultSet res = prep.executeQuery();
                while(res.next()){
                    ArrayList<String> resultat = new ArrayList<>();
                    for (int i = 0; i < 6; i++) {
                        resultat.add(res.getString(i +1));
                    }
                    users.add(resultat);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[][] array = new String[users.size()][];
            for (int i = 0; i < users.size(); i++) {
                ArrayList<String> row = users.get(i);
                array[i] = row.toArray(new String[row.size()]);
            }
        }
        return array;
    }
    */

    public ArrayList<User> getUsers(){
        ArrayList<User> users = new ArrayList<User>();
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement("SELECT * FROM user;");
                res = prep.executeQuery();
                while (res.next()){
                    User user = new User();
                    user.setId(res.getInt("user_id"));
                    user.setFirstName(res.getString("first_name"));
                    user.setLastName(res.getString("last_name"));
                    user.setEmail(res.getString("email"));
                    user.setPhonenumber(res.getString("phonenumber"));
                    user.setCategory(User.UserCategory.valueOf(res.getInt("category")));
                    users.add(user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return users;
    }
    public User getUserById(String userId) {
        User user = null;
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetUserById);
                prep.setString(1, userId);
                res = prep.executeQuery();
                if (res.next()){
                    User u = new User();
                    u.setId(res.getInt("user_id"));
                    u.setFirstName(res.getString("first_name"));
                    u.setLastName(res.getString("last_name"));
                    u.setEmail(res.getString("email"));
                    u.setPhonenumber(res.getString("phonenumber"));
                    u.setCategory(User.UserCategory.valueOf(res.getInt("category")));
                    user = u;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
      return user;
    }
     /**
     * Creates a new user in the database
     * @param
     * @return Integer > -1 if success, -1 if fail
     */
    //Returnerer int 1 dersom bruker har blitt opprettet. Kan ogsÃ¥ endres til Ã¥ returnere objekt med brukernavn, passord, email og phone (for Ã¥ da sende email til brukeren med brukerdata)
    public int createNewUser(String first_name, String last_name, String email, String phone, int category) { //AnsattNr? , Navn = etternavn, fornavn mellomnavn. Parse etternavn (fÃ¸r komma)
        int creation = -1;
        String randomPass = GeneratePassword.generateRandomPass();
        //sendEmailWithGeneratedPass to registered user in this method
        String hashedPass[] = en.passEncoding(randomPass);
        String salt = hashedPass[0];
        String hash = hashedPass[1];
        if (setUp()) {
            try {
                startTransaction();
                prep = getConnection().prepareStatement(sqlCreateNewUser, Statement.RETURN_GENERATED_KEYS);
                prep.setString(1, first_name);
                prep.setString(2, last_name);
                prep.setString(3, hash);
                prep.setString(4, salt);
                prep.setString(5, email);
                prep.setString(6, phone);
                prep.setInt(7, category);
                creation = prep.executeUpdate();
                return QueryUtil.getGeneratedKeys(prep);
            } catch (Exception e) {
                System.out.println("Issue creating new ansatt");
                e.printStackTrace();
            } finally {
                endTransaction();
                finallyStatement(prep);
            }
        }
        return -1;
    }

     /**
     * Changes a user's password
     * @param userId
     * @param prev_password, the previous password
     * @param new_password, the new password
     * @return Integer > -1 if success, -1 if fail
     */
    public int changePasswordUserId(String user_id, String prev_password, String new_password) {
        int change = -1;
        boolean oldPassCorrect = false;
        if (setUp()) {
            try {
                startTransaction();
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
        }
        if(oldPassCorrect) {
            //Change Pass
            String[] passInfoNew = en.passEncoding(prev_password);
            String hashNew = passInfoNew[0];
            String saltNew = passInfoNew[1];
            try {
                prep = getConnection().prepareStatement(sqlChangePass);
                prep.setString(1, hashNew);
                prep.setString(2, saltNew);
                prep.setString(3, user_id);
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

    /**
    * Receives a user object which has changed information. Then updates the information in database.
    * @param user User object
    * @return Integer >-1 if success, -1 if fail
    */
     public int changeUserInfo(User user) {
        int change = -1;
            if(setUp()) {
                try {
                    startTransaction();
                    conn = getConnection();
                    //conn.setAutoCommit(false);
                    prep = conn.prepareStatement(sqlChangeUserInfo);
                    prep.setString(1, user.getFirstName());
                    prep.setString(2, user.getLastName());
                    prep.setString(3, user.getEmail());
                    prep.setString(4, user.getPhonenumber());
                   // prep.setInt(5, user.getCategory());
                    change = prep.executeUpdate();
                } catch (Exception e) {
                    System.out.println("Error at changeUserInfo()");
                    e.printStackTrace();
                } finally {
                    finallyStatement(res,prep);
                }
            }
        return change;
    }

    /**
    * Changes department for user
    * @param userId
    * @return Integer >-1 if success, -1 if fail
    */

    public ArrayList<UserBasic> getUserBasics() {
        ArrayList<UserBasic> userBasics = new ArrayList<>();
        if (setUp()) {
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetUserBasics);
                res = prep.executeQuery();
                while (res.next()) {
                    userBasics.add(new UserBasic(
                            res.getInt("user_id"),
                            res.getString("first_name"),
                            res.getString("last_name"),
                            User.UserCategory.valueOf(res.getInt("category"))
                    ));
                }
            } catch (SQLException sqle) {
                System.out.println("Issue with getting user basics");
                sqle.printStackTrace();
            } finally {
                finallyStatement(res, prep);
            }
        }
        return userBasics;
    }
    //If string contains @, it's an email
   /* if(username.contains("@")) {
     checkLogin(username, password); //Email
     } else {
     checkLogin(username, password); //Phone
     }*/

}
