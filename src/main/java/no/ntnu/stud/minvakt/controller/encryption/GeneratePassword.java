/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package no.ntnu.stud.minvakt.controller.encryption;

import java.lang.invoke.MethodHandles;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 8460p
 */
public class GeneratePassword {
    private static final Logger log = Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

    public static String generateRandomPass() {
        //Requirements:
        //Min 1 stor bokstav
        //min 8 tegn
        //min 2 spesialtegn
        String passwExample = "pK9t!lrQs1"; //_-!#
        Random rn = new Random();
        String alph = "abcdefghijklmnopqrstuvwxyz";
        String numb = "1234567890"; //i starten, midten, eller slutten
        String chars = "!-_#";
        //small, uppercase 20% of the passw. Password contains of 12 characters
        int numAlph = 0; //6 - 2 upper
        int numNumb = 0; //2
        int numChars = 0; //2
        int numUpperCase = 0; //to bokstaver
        int maxLength = 10;
        String password = "";
        int n = 5 - 1 + 1;
        for (int i = 0; i < maxLength; i++) {
            int randomNum = rn.nextInt(5 - 1 + 1) + 1;
            if ((numNumb < 1 && randomNum == 1) || (numNumb < 2 && i > 6 && i != 7)) { //Increase numbers
                int rndNumb = rn.nextInt(numb.length());
                password += numb.charAt(rndNumb);
                numNumb++;
            } else if ((numChars < 2 && randomNum == 2) || (numChars < 2 && i > 2 && i != 3)) { //Increase spesialChars
                int rndChars = rn.nextInt(chars.length());
                password += chars.charAt(rndChars);
                numChars++;
            } else {        //3/5 probability
                char charSpecific = 'a';
                if (i >= 1) {
                    charSpecific = password.charAt(i - 1);
                } else {
                    int rndAlph = rn.nextInt(alph.length());
                    charSpecific = alph.charAt(rndAlph);
                }
                boolean endCharIsUpper = isUpperCase(charSpecific);
                if (!endCharIsUpper && numUpperCase < 2) { //Maks 3 uppercase. Kan ikke vÃ¦re 2 uppercase etter hverandre.
                    //Previous char was not uppercase. We'll make this one
                    int rndAlph = rn.nextInt(alph.length());
                    char toUpper = alph.charAt(rndAlph);
                    char upperChar = (char) (toUpper & 0x5f);
                    numUpperCase++;
                    password += upperChar;
                    numAlph++;
                } else {
                    int rndAlph = rn.nextInt(alph.length());
                    password += alph.charAt(rndAlph);
                    numAlph++;
                }
            }
        }
        if (numChars < 2) {
            log.log(Level.INFO, "----LESS THAN 2----");
        }
        System.out.print(password);
        return password;
    }

    static boolean isLowerCase(char ch) {
        return ch >= 'a' && ch <= 'z';
    }

    static boolean isUpperCase(char ch) {
        return ch >= 'A' && ch <= 'Z';
    }
}
