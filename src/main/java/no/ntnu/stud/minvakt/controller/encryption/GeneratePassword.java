/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package no.ntnu.stud.minvakt.controller.encryption;

import java.lang.invoke.MethodHandles;
import java.security.SecureRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 8460p
 */
public class GeneratePassword {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static final String VALID_LETTERS = "abcdefghijklmnopqrstuvwxyz";
    public static final String VALID_NUMBERS = "1234567890"; //i starten, midten, eller slutten
    public static final String VALID_CHARACTERS = "!-_#";
    public static final int MAX_LENGTH = 10;

    public static String generateRandomPass() {
        //Requirements:
        //Min 1 stor bokstav
        //min 8 tegn
        //min 2 spesialtegn
        SecureRandom rn = new SecureRandom();

        //small, uppercase 20% of the passw. Password contains of 12 characters
        int numAlph = 0; //6 - 2 upper
        int numNumb = 0; //2
        int numChars = 0; //2
        int numUpperCase = 0; //to bokstaver
        String password = "";

        for (int i = 0; i < MAX_LENGTH; i++) {
            int randomNum = rn.nextInt(5 - 1 + 1) + 1;
            if ((numNumb < 1 && randomNum == 1) || (numNumb < 2 && i > 6 && i != 7)) { //Increase numbers
                int rndNumb = rn.nextInt(VALID_NUMBERS.length());
                password += VALID_NUMBERS.charAt(rndNumb);
                numNumb++;
            } else if ((numChars < 2 && randomNum == 2) || (numChars < 2 && i > 2 && i != 3)) { //Increase spesialChars
                int rndChars = rn.nextInt(VALID_CHARACTERS.length());
                password += VALID_CHARACTERS.charAt(rndChars);
                numChars++;
            } else {        //3/5 probability
                char charSpecific;
                if (i >= 1) {
                    charSpecific = password.charAt(i - 1);
                } else {
                    int rndAlph = rn.nextInt(VALID_LETTERS.length());
                    charSpecific = VALID_LETTERS.charAt(rndAlph);
                }
                boolean endCharIsUpper = Character.isUpperCase(charSpecific);
                if (!endCharIsUpper && numUpperCase < 2) {
                    // Maks 3 uppercase. Kan ikke vÃ¦re 2 uppercase etter hverandre.
                    //Previous char was not uppercase. We'll make this one
                    int rndAlph = rn.nextInt(VALID_LETTERS.length());
                    char toUpper = VALID_LETTERS.charAt(rndAlph);
                    char upperChar = (char) (toUpper & 0x5f);
                    numUpperCase++;
                    password += upperChar;
                    numAlph++;
                } else {
                    int rndAlph = rn.nextInt(VALID_LETTERS.length());
                    password += VALID_LETTERS.charAt(rndAlph);
                    numAlph++;
                }
            }
        }
        if (numChars < 2) {
            log.log(Level.INFO, "----LESS THAN 2----");
        }

        return password;
    }
}
