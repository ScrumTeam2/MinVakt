package no.ntnu.stud.minvakt.controller.encryption;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.lang.invoke.MethodHandles;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to handle password encryption and hash check
 */
public class Encryption {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    /**
     * Checks whether two passwords are the same using an input password,
     * salt and a hash.
     *
     * @param pass       Password from user
     * @param saltString Salt from database.
     * @param hashString Hash from database
     * @return boolean whether they matched.
     */
    public boolean passDecoding(String pass, String hashString, String saltString) {
        byte[] salt = stringToByte(saltString);
        byte[] hashPass = stringToByte(hashString);


        byte[] hash;
        KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = f.generateSecret(spec).getEncoded();
        } catch (Exception e) {

            log.log(Level.SEVERE, "Issue with secret key factory in password decryption.", e);
            return false;
        }
        if (Arrays.equals(hashPass, hash)) return true;
        return false;

    }

    /**
     * Creates a salt and hash for storage in the database using only an input password.
     *
     * @param password password you want to encrypt
     * @return a string array of the resulting hash and salt.
     */
    public String[] passEncoding(String password) {
        Random rand = new Random();
        byte[] salt = new byte[16];
        byte[] hash;
        rand.nextBytes(salt);
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = f.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Issue with SecretKeyFactory in password encrpytion.", e);
            return null;
        }

        Base64.Encoder enc = Base64.getEncoder();
        String[] out = new String[2];

        out[0] = (enc.encodeToString(salt));
        out[1] = (enc.encodeToString(hash));
        return out;
    }

    /**
     * Converts String to byte array
     *
     * @param string
     * @return
     */
    private byte[] stringToByte(String string) {
        Base64.Decoder dec = Base64.getDecoder();
        return dec.decode(string);
    }
}
