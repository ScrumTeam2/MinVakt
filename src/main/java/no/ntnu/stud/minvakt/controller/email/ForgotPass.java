package no.ntnu.stud.minvakt.controller.email;

import no.ntnu.stud.minvakt.controller.encryption.Encryption;
import no.ntnu.stud.minvakt.controller.encryption.GeneratePassword;
import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.database.UserDBManager;

/**
 * Created by evend on 1/24/2017.
 */
public class ForgotPass {
    private static Encryption enc = new Encryption();
    private static UserDBManager userDB = new UserDBManager();

    /**
     * @param email
     * @return 1 = Everything succeeded, 0 = mail failed, -1 = nothing succeeded
     */
    public static int sendEmailWithNewPass(String email){
        int userId = userDB.getUserIdFromMail(email);
        String newPass = GeneratePassword.generateRandomPass();
        String[] saltHash = enc.passEncoding(newPass);
        if(userDB.setNewPassword(userId, saltHash)){
            if(Mail.sendMail(email,"Glemt passord for MinVakt", "Her er ditt nye passord for MinVakt, husk å endre det så for som mulig." +
                    "\n Passord: "+newPass) == 1){
                return 1;
            }
            else return 0;
        }
        return -1;
    }
}
