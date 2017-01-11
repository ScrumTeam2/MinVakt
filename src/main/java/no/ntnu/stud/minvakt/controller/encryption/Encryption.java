package no.ntnu.stud.minvakt.controller.encryption;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * Created by M/ed on 07.03.2016.
 */
public class Encryption {

    /**
     * Checks whether two passwords are the same using an input password,
     * salt and a hash.
     *
     * @param pass       Password from user
     * @param saltString Salt from database.
     * @param hashString Hash from database
     * @return boolean whether they matched.
     */
    public boolean passDecoding(String pass, String saltString, String hashString) {
        byte[] salt = stringToByte(saltString);

        byte[] hashPass = stringToByte(hashString);


        byte[] hash;
        KeySpec spec = new PBEKeySpec(pass.toCharArray(), salt, 65536, 128);
        try {
            SecretKeyFactory f = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            hash = f.generateSecret(spec).getEncoded();
        } catch (Exception e) {
            System.err.println("Issue with secret key factory in password decryption.");
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
            System.err.println("Issue with SecretKeyFactory in password encrpytion.");
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


    /**
     * Test program for encryption
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Encryption en = new Encryption();
        String[] passInfo = en.passEncoding("password");
        if (en.passDecoding("password", "2oUGF8AAgobU1E3rcAtyiw==", "oQaZgG266KjDzEkGTgXYMQ==")) ;
    }
}