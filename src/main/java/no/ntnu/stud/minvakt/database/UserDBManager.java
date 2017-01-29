package no.ntnu.stud.minvakt.database;

import com.mysql.cj.api.jdbc.Statement;
import no.ntnu.stud.minvakt.controller.encryption.Encryption;
import no.ntnu.stud.minvakt.controller.encryption.GeneratePassword;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.data.user.UserBasic;
import no.ntnu.stud.minvakt.data.user.UserBasicList;
import no.ntnu.stud.minvakt.util.QueryUtil;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Database layer class for users
 */
public class UserDBManager extends DBManager {
    public UserDBManager() {
        super();
    }
    private final String sqlLoginId = "SELECT * FROM user where user_id=? AND removed = 0;";
    private final String sqlLogin = "SELECT * FROM user WHERE email = ? OR phonenumber = ? AND removed = 0;";
    private final String sqlChangePass = "UPDATE user SET hash = ?, salt = ? WHERE user_id = ?;";
    private final String sqlGetUsers = "SELECT * FROM user WHERE removed = 0;";
    private final String sqlGetUserById = "SELECT * FROM user WHERE user_id = ?;";
    private final String sqlCreateNewUser = "INSERT INTO user VALUES (DEFAULT,?,?,?,?,?,?,?,?,?,DEFAULT);";
    private final String sqlChangeUserInfo = "UPDATE user SET first_name = ?, last_name = ?, email =?, phonenumber =?, category=?, percentage_work=?, dept_id=? WHERE user_id =?;";
    private final String sqlChangeUserInfoSimple = "UPDATE user SET email =?, phonenumber =? WHERE user_id =?;";

    private final String sqlIsAdmin = "SELECT * FROM admin WHERE user_id = ?";
    private final String sqlGetUserBasics = "SELECT user_id, first_name, last_name, category FROM user WHERE removed = 0 ORDER BY last_name ASC, first_name ASC;";
    private final String sqlGetUserBasicsWithCategory = "SELECT user_id, first_name, last_name, category FROM user WHERE category = ? " +
            " AND removed = 0 ORDER BY last_name ASC, first_name ASC;";

    //private final String sqlChangeDep = "UPDATE dept_id FROM user where user_id=?";
    private final String sqlDeleteUser = "UPDATE user SET removed = 1 WHERE user_id = ?";
    private final String sqlDeleteUserCompletely = "DELETE FROM user WHERE user_id = ?";

    private final String sqlGetAdminId = "SELECT user_id FROM user WHERE category = ? AND removed = 0 LIMIT 1;";
    private final String sqlGetUserIdByMail = "SELECT user_id FROM user WHERE email = ? AND removed = 0";

    private static final String sqlCheckEmail = "SELECT 1 FROM user WHERE email = ?";
    private static final String sqlCheckPhoneNumber = "SELECT 1 FROM user WHERE phonenumber = ?";

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
                    if (en.passDecoding(pass, res.getString("hash"), res.getString("salt"))) {
                        //New user
                        User user = new User(res.getInt("user_id"), res.getString("first_name"),
                                res.getString("last_name"), null,null,res.getString("email"), res.getString("phonenumber"),
                                User.UserCategory.valueOf(res.getInt("category")), res.getFloat("percentage_work"),
                                res.getInt("dept_id"));
                        return user;
                    }
                    else{
                        throw new Exception("User with username = "+username+" typed incorrect password!"+pass);
                    }
                }

            } catch (Exception e) {
                log.log(Level.WARNING, "Could not log in user " + username, e);
            }finally{
                endTransaction();
                finallyStatement(res, prep);
            }
        }
        return null;
    }

    /**
     * Checks whether the user ID and password matches a row in the database
     * @param userId
     * @param pass plaintext
     * @return Integer>True if success
     */
    public boolean checkLoginId(int userId, String pass) {
        if (setUp()) {
            try {
                startTransaction();
                prep = getConnection().prepareStatement(sqlLoginId);
                prep.setInt(1, userId);
                res = prep.executeQuery();
                if (res.next()) {
                    if (en.passDecoding(pass, res.getString("hash"), res.getString("salt"))) {
                        return true;
                    }
                }

            } catch (Exception e) {
                log.log(Level.WARNING, "Unable to check login for ID " + userId, e);
            }
            finally {
                endTransaction();
                finallyStatement(res,prep);
            }
        }
        return false;
    }

    public int checkUserAdmin(String userId) {
        int isAdmin = -1;
        return isAdmin;
    }

    /**
     * Deletes user, only used for updating database during tests
     * @param userId - ID for user to delete
     * @return True: If successful
     */
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
    public boolean deleteUserCompletely(int userId) {
        //"Deletes" the row of this specific user? Possible?
        if (setUp()) {
            try {
                prep = getConnection().prepareStatement(sqlDeleteUserCompletely);
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
  /*
     -- NEVER USED


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
*/
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

    /**
     * Returns arraylist of User objects
     * @param includeAdmins: False if you want only employees (no admins)
     *                       True if all users
     * @return ArrayList<User>
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
                    user.setDeptId(res.getInt("dept_id"));
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


    // Currently not used
    /*
    public ArrayList<User> getUsersInDepartment(int departmentId, boolean includeAdmins){
        ArrayList<User> users = new ArrayList<User>();
        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(includeAdmins ? "SELECT * FROM user WHERE dept_id = ?" : "SELECT * FROM user WHERE dept_id = ? AND category != 0");
                prep.setInt(1, departmentId);
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
                    user.setDeptId(res.getInt("dept_id"));
                    users.add(user);
                }
            } catch (Exception e) {
                log.log(Level.WARNING, "Failed to select users", e);
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return users;
    }
*/

    /**
     * Returns User object by given user ID
     * @param userId - ID for user
     * @return User object
     */
    public User getUserById(int userId) {
        User user = null;
        ResultSet res = null;
        if(setUp()){
            try {
                conn = getConnection();
                PreparedStatement prep = conn.prepareStatement(sqlGetUserById);
                prep.setInt(1, userId);
                res = prep.executeQuery();
                System.out.println();
                if (res.next()){
                    User u = new User();
                    u.setId(res.getInt("user_id"));
                    u.setFirstName(res.getString("first_name"));
                    u.setLastName(res.getString("last_name"));
                    u.setEmail(res.getString("email"));
                    u.setPhoneNumber(res.getString("phonenumber"));
                    u.setCategory(User.UserCategory.valueOf(res.getInt("category")));
                    u.setWorkPercentage(res.getFloat("percentage_work"));
                    u.setDeptId(res.getInt("dept_id"));
                    user = u;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                finallyStatement(res,prep);
            }
        }
        System.out.println("Users ! " + user);

        return user;
    }
    /*
     * Creates a new user in the database
     * return An object containing the user's ID (index 0), and the user's password (index 1)
     */


    /**
     * Creates new user in the database
     * @param firstName - String first name
     * @param lastName - String last name
     * @param email - String email
     * @param phone - String phone number
     * @param category - UserCategory category
     *                 Values: 0: Admin, 1: Nurse, 2: Healthworker, 3: Assistant
     * @param workPercentage - Float: percentage of work (0 to 1)
     * @param deptId - ID of department
     * @return Object[] - array with user
     */
    public Object[] createNewUser(String firstName, String lastName, String email, String phone,
                                  User.UserCategory category, float workPercentage, int deptId) {
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
                prep.setInt(9,deptId);
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

    /**
     * Checks if there is an user with the given phone number
     * @param email The email to check
     * @return true if an user already exists with this phone number
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
            } catch (SQLException e) {
                log.log(Level.WARNING, "User ID: " + user_id, e);
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
                prep.setInt(5, user.getCategory().getValue());
                prep.setFloat(6, user.getWorkPercentage());
                prep.setInt(7, user.getDeptId());
                prep.setInt(8, user.getId());

                change = prep.executeUpdate();
            } catch (Exception e) {
                log.log(Level.WARNING, "Could not change user info", e);
            } finally {
                endTransaction();
                finallyStatement(res,prep);
            }
        }
        return change;
    }

    /**
     * Changes user info: Email, phone number
     * @param email - new email
     * @param phone - new phone number
     * @param userId - ID for user you want to change
     * @return int: > -1 if success, -1 if not success
     */
    public int changeUserInfoSimple(String email, String phone, int userId) {
        int change = -1;
        if(setUp()) {
            try {
                startTransaction();
                conn = getConnection();
                //conn.setAutoCommit(false);
                prep = conn.prepareStatement(sqlChangeUserInfoSimple);
                prep.setString(1, email);
                prep.setString(2, phone);
                prep.setInt(3, userId);

                change = prep.executeUpdate();
            } catch (Exception e) {
                System.out.println("Error at changeUserInfoSimple()");
                e.printStackTrace();
            } finally {
                endTransaction();
                finallyStatement(res,prep);
            }
        }
        return change;
    }


    /**
     * Returns ArrayList with users
     * @return ArrayList<UserBasicList>
     */
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
            } catch (SQLException e) {
                log.log(Level.WARNING, "Issue with getting user basics", e);
            } finally {
                finallyStatement(res, prep);
            }
        }
        return userBasics;
    }

    /**
     * Returns admin ID
     * @return int: Admin ID
     */
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
                log.log(Level.WARNING, "Issue with getting an admin ID", sqle);

            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }


    /**
     * Sets new password for a user
     * @param userId - ID for user
     * @param saltHash
     * @return True if successful
     */
    public boolean setNewPassword(int userId, String[] saltHash){
        boolean out = false;

        if(setUp()){
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlChangePass);
                prep.setString(1,saltHash[1]); //hash
                prep.setString(2,saltHash[0]); //salt
                prep.setInt(3,userId);
                out = prep.executeUpdate() != 0;
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Issue with change user password", sqle);
            }
            finally {
                finallyStatement(prep);
            }
        }
        return out;
    }


    /**
     * Gets userID by mail if a user has forgotten the password an needs a new one
     * @param email - email address registered on user
     * @return int: userId on user with corresponding email address
     */
    public int getUserIdFromMail(String email){
        int out = 0;
        if(setUp()){
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetUserIdByMail);
                prep.setString(1, email);
                res = prep.executeQuery();
                if(res.next()) {
                    out = res.getInt(1);
                }
            }catch (SQLException sqle){
                log.log(Level.WARNING, "Issue with getting user from mail "+email, sqle);

            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }

    /**
     * Returns array with users given category
     * @param category - UserCategory category
     *                 Values: 0: Admin, 1: Nurse, 2: Healthworker, 3: Assistant
     * @return ArrayList<UserBasics>
     */
    public ArrayList<UserBasic> getUserBasicsWithCategory(User.UserCategory category){
        ArrayList<UserBasic> out = new ArrayList<>();
        if(setUp()){
            ResultSet res = null;
            try {
                conn = getConnection();
                prep = conn.prepareStatement(sqlGetUserBasicsWithCategory);
                prep.setInt(1,category.getValue());
                res = prep.executeQuery();
                while(res.next()){
                    out.add(new UserBasic(
                            res.getInt("user_id"),
                            res.getString("first_name"),
                            res.getString("last_name"),
                            User.UserCategory.valueOf(res.getInt("category"))
                    ));
                }
            }
            catch (SQLException sqle){
                log.log(Level.WARNING, "Issue getting user basics from category", sqle);
            }
            finally {
                finallyStatement(res, prep);
            }
        }
        return out;
    }
}