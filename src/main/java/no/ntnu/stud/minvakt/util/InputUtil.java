package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.user.User;
import no.ntnu.stud.minvakt.util.rest.ErrorInfo;

import javax.ws.rs.core.Response;

/**
 * Created by Audun on 21.01.2017.
 */
public class InputUtil {
    private static final int PHONE_NUMBER_LENGTH = 8;

    /**
     * Checks if a string is null or empty
     * @param s The string to check
     * @return true if null or empty
     */
    public static boolean isNullOrWhitespace(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean validateEmail(String email) {
        // TODO: Regex check?
        return !isNullOrWhitespace(email);
    }

    public static boolean validatePhoneNumber(String number) {
        if (isNullOrWhitespace(number)) return false;

        // TODO: Regex check?
        if(number.length() != PHONE_NUMBER_LENGTH) {
            // Invalid length
            return false;
        }

        // Check if we have letters
        for(char c : number.toCharArray()) {
            if(!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates the fields of an user (probably deserialized from JSON)
     * @param user
     * @return null if the user is valid, a web response containing the error if else
     */
    public static Response validateUser(User user) {
        if(!validateEmail(user.getEmail())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Ugyldig mail")).build();
        }

        if(!validatePhoneNumber(user.getPhoneNumber())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Ugyldig telefonnummer")).build();
        }

        if(isNullOrWhitespace(user.getFirstName())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Ugyldig fornavn")).build();
        }

        if(isNullOrWhitespace(user.getLastName())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Ugylding etternavn")).build();
        }

        if(user.getWorkPercentage() < 0 || user.getWorkPercentage() > 1) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Ugyldig stillingsprosent")).build();
        }
        return null;
    }

    /**
     * Validates a password with the rules defined in the vision document
     * @param password The password to validate
     * @return True if the password is valid
     */
    public static boolean validatePassword(String password) {
        if(password.length() < 8) {
            return false;
        }
        if(password.toUpperCase().equals(password)) {
            // No lowercase letters
            return false;
        }

        if(password.toLowerCase().equals(password)) {
            // No uppercase letters
            return false;
        }

        if(getSpecialCharacterCount(password) < 2) {
            return false;
        }
        return true;
    }

    private static int getSpecialCharacterCount(String string) {
        final String specialChars = "0123456789<>@!#$%^&*()_+[]{}?:;|'\"\\,./~`-=";
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (specialChars.indexOf(string.charAt(i)) > -1) {
                count++;
            }
        }
        return count;
    }
}
