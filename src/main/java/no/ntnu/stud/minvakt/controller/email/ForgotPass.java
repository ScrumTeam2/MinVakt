package no.ntnu.stud.minvakt.controller.email;

import no.ntnu.stud.minvakt.controller.encryption.Encryption;
import no.ntnu.stud.minvakt.controller.encryption.GeneratePassword;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.UserDBManager;

/**
 * Logic class for handling password reset
 */
public class ForgotPass {
    private static Encryption enc = new Encryption();
    private static UserDBManager userDB = new UserDBManager();

    /**
     * Generates a password, and sends email with the new password to user
     * @param email email-address
     * @return 1 = Everything succeeded, 0 = mail failed, -1 = nothing succeeded
     */
    public static int sendEmailWithNewPass(String email){
        int userId = userDB.getUserIdFromMail(email);
        String newPass = GeneratePassword.generateRandomPass();
        String[] saltHash = enc.passEncoding(newPass);
        if(userDB.setNewPassword(userId, saltHash)){
            if(Mail.sendMail(email,"Glemt passord for MinVakt", "Her er ditt nye passord for MinVakt, husk å endre det så for som mulig." +
                    "\n Passord: "+newPass)){
                return 1;
            }
            else return 0;
        }
        return -1;
    }
}
