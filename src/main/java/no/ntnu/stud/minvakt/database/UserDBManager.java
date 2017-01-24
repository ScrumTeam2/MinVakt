package no.ntnu.stud.minvakt.database;

import com.mysql.cj.api.jdbc.Statement;
import no.ntnu.stud.minvakt.controller.encryption.Encryption;
import no.ntnu.stud.minvakt.controller.encryption.GeneratePassword;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasic;
import no.ntnu.stud.minvakt.data.user.UserBasicList;
import no.ntnu.stud.minvakt.util.QueryUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

public class UserDBManager extends DBManager {
    public UserDBManager() {
        super();
    }
    private final String sqlLoginId = "SELECT * FROM user where user_id=?;";
    private final String sqlLogin = "SELECT * FROM user WHERE email = ? OR phonenumber = ?;";
    private final String sqlChangePass = "UPDATE user SET hash = ?, salt = ? WHERE user_id = ?;";
    private final String sqlGetUsers = "SELECT * FROM user;";
    private final String sqlGetUserById = "SELECT * FROM user WHERE user_id = ?;";
    private final String sqlCreateNewUser = "INSERT INTO user VALUES (DEFAULT,?,?,?,?,?,?,?,?);";
    private final String sqlChangeUserInfo = "UPDATE user SET first_name = ?, last_name = ?, email =?, phonenumber =? WHERE user_id =?;";
    private final String sqlIsAdmin = "SELECT * FROM admin WHERE user_id = ?";
    private final String sqlGetUserBasics = "SELECT user_id, first_name, last_name, category FROM user ORDER BY last_name ASC, first_name ASC;";
    //private final String sqlChangeDep = "UPDATE dept_id FROM user where user_id=?";
    private final String sqlDeleteUser = "DELETE FROM user WHERE user_id = ?";
    private final String sqlGetAdminId = "SELECT user_id FROM user WHERE category = ? LIMIT 1;";

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
                        User user = new User(res.getInt("user_id"), res.getString("first_name"),
                                res.getString("last_name"), null,null,res.getString("email"), res.getString("phonenumber"),
                                User.UserCategory.valueOf(res.getInt("category")), res.getFloat("percentage_work"));
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
     * @param pass plaintext
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
            finally {
                endTransaction();
                finallyStatement(res,prep);
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


  /*  public int changeDepartment(int user_id) {
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
    }*/
    
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
            finally {
                finallyStatement(res,prep);
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

    public ArrayList<User> getUsers(boolean includeAdmins){
        ArrayList<User> users = new ArrayList<User>();
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(includeAdmins ? "SELECT * FROM user" : "SELECT * FROM user WHERE category != 0");
                res = prep.executeQuery();
                while (res.next()){
                    User user = new User();
                    user.setId(res.getInt("user_id"));
                    user.setFirstName(res.getString("first_name"));
                    user.setLastName(res.getString("last_name"));
                    user.setEmail(res.getString("email"));
                    user.setPhoneNumber(res.getString("phonenumber"));
                    user.setCategory(User.UserCategory.valueOf(res.getInt("category")));
                    user.setWorkPercentage(res.getFloat("percentage_work"));
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
    public User getUserById(int userId) {
        User user = null;
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetUserById);
                prep.setInt(1, userId);
                res = prep.executeQuery();
                if (res.next()){
                    User u = new User();
                    u.setId(res.getInt("user_id"));
                    u.setFirstName(res.getString("first_name"));
                    u.setLastName(res.getString("last_name"));
                    u.setEmail(res.getString("email"));
                    u.setPhoneNumber(res.getString("phonenumber"));
                    u.setCategory(User.UserCategory.valueOf(res.getInt("category")));
                    user = u;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                finallyStatement(res,prep);
            }
        }
      return user;
    }
     /**
     * Creates a new user in the database
     * @return An object containing the user's ID (index 0), and the user's password (index 1)
     */
    public Object[] createNewUser(String firstName, String lastName, String email, String phone, User.UserCategory category, float workPercentage) {
        Object[] obj = new Object[2];
        obj[0] = -1;
        String randomPass = GeneratePassword.generateRandomPass();
        //sendEmailWithGeneratedPass to registered user in this method
        String hashedPass[] = en.passEncoding(randomPass);
        String salt = hashedPass[0];
        String hash = hashedPass[1];
        if (setUp()) {
            try {
                prep = getConnection().prepareStatement(sqlCreateNewUser, Statement.RETURN_GENERATED_KEYS);
                prep.setString(1, firstName);
                prep.setString(2, lastName);
                prep.setString(3, hash);
                prep.setString(4, salt);
                prep.setString(5, email);
                prep.setString(6, phone);
                prep.setInt(7, category.getValue());
                prep.setFloat(8, workPercentage);
                int creation = prep.executeUpdate();
                if (creation != 0) {
                    obj[0] = QueryUtil.getGeneratedKeys(prep);
                    obj[1] = randomPass;
                }
                return obj;

            } catch (Exception e) {
                log.log(Level.WARNING, "Issue creating new user", e);
            } finally {
                finallyStatement(prep);
            }
        }
        return obj;
    }

    private static final String sqlCheckPhoneNumber = "SELECT 1 FROM user WHERE phonenumber = ?";

    /**
     * Checks if there is an user with the given phone number
     * @param phoneNumber The phone number to check
     * @return true if an user exists with this phone number
     */
    public boolean isPhoneNumberTaken(String phoneNumber) {
        if (!setUp()) {
            log.log(Level.WARNING, "Failed to set up db connection");
            return true;
        }

        try {
            prep = getConnection().prepareStatement(sqlCheckPhoneNumber);
            prep.setString(1, phoneNumber);
            res = prep.executeQuery();
            return res.next();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Failed to check phone number", e);
        } finally {
            finallyStatement(res, prep);
        }
        return true;
    }

    private static final String sqlCheckEmail = "SELECT 1 FROM user WHERE email = ?";
    /**
     * Checks if there is an user with the given phone number
     * @param email The email to check
     * @return true if an user exists with this phone number
     */
    public boolean isEmailTaken(String email) {
        if (!setUp()) {
            log.log(Level.WARNING, "Failed to set up db connection");
            return true;
        }

        try {
            prep = getConnection().prepareStatement(sqlCheckEmail);
            prep.setString(1, email);
            res = prep.executeQuery();
            return res.next();
        } catch (SQLException e) {
            log.log(Level.SEVERE, "Failed to check email", e);
        } finally {
            finallyStatement(res, prep);
        }
        return true;
    }

     /**
     * Changes a user's password
     * @param user_id
     * @param prev_password, the previous password
     * @param new_password, the new password
     * @return Integer > -1 if success, -1 if fail
     */

    public int changePasswordUserId(String user_id, String prev_password, String new_password) {
        int change = -1;
        if (setUp()) {
            try {
                startTransaction();
                prep = getConnection().prepareStatement(sqlLoginId);
                prep.setString(1, user_id);
                res = prep.executeQuery();
                if (res.next()) {
                    if (en.passDecoding(prev_password, res.getString("hash"), res.getString("salt"))) {
                        String[] passInfoNew = en.passEncoding(new_password);
                        String saltNew = passInfoNew[0];
                        String hashNew = passInfoNew[1];
                        prep = getConnection().prepareStatement(sqlChangePass);
                        prep.setString(1, hashNew);
                        prep.setString(2, saltNew);
                        prep.setString(3, user_id);
                        change = prep.executeUpdate();
                    }

                }
            } catch (SQLException sqle) {
                System.out.println("Error at changePasswordUserId()");
                sqle.printStackTrace();
            } finally {
                endTransaction();
                finallyStatement(res, prep);
            }
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
                    prep.setString(4, user.getPhoneNumber());
                   // prep.setInt(5, user.getCategory());
                    change = prep.executeUpdate();
                } catch (Exception e) {
                    System.out.println("Error at changeUserInfo()");
                    e.printStackTrace();
                } finally {
                    endTransaction();
                    finallyStatement(res,prep);
                }
            }
        return change;
    }


    public ArrayList<UserBasicList> getUserBasics() {
        ArrayList<UserBasicList> userBasics = new ArrayList<>();
        if (setUp()) {
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetUserBasics);
                res = prep.executeQuery();
                char orderLetter = 0;
                String lastName;
                ArrayList<UserBasic> usersWithSameHead = new ArrayList<>();
                while (res.next()) {
                    lastName =  res.getString("last_name");
                    if (lastName.equals("")) lastName = " ";
                    if (orderLetter == 0) orderLetter = lastName.charAt(0);
                    if (lastName.charAt(0) != orderLetter){
                        userBasics.add(new UserBasicList(usersWithSameHead,
                                orderLetter));
                        orderLetter = lastName.charAt(0);
                        usersWithSameHead = new ArrayList<>();
                    }
                    usersWithSameHead.add(new UserBasic(
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
    public int getAdminId(){
         int out = 0;
         if(setUp()){
             ResultSet res = null;
             try {
                 conn = getConnection();
                 prep = conn.prepareStatement(sqlGetAdminId);
                 prep.setInt(1, User.UserCategory.ADMIN.getValue());
                 res = prep.executeQuery();
                 if(res.next()) {
                     out = res.getInt(1);
                 }
             }catch (SQLException sqle){
                 log.log(Level.WARNING, "Issue with getting an admin ID");
                 sqle.printStackTrace();

             }
             finally {
                 finallyStatement(res, prep);
             }
         }
         return out;
    }
    //If string contains @, it's an email
   /* if(username.contains("@")) {
     checkLogin(username, password); //Email
     } else {
     checkLogin(username, password); //Phone
     }*/


}
