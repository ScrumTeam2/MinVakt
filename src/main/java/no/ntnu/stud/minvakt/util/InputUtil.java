package no.ntnu.stud.minvakt.util;

import no.ntnu.stud.minvakt.data.user.User;

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

    public static boolean verifyEmail(String email) {
        // TODO: Regex check?
        return !isNullOrWhitespace(email);
    }

    public static boolean verifyPhoneNumber(String number) {
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
     * Verifies the fields of an user (probably deserialized from JSON)
     * @param user
     * @return null if the user is valid, a web response containing the error if else
     */
    public static Response verifyUser(User user) {
        if(!verifyEmail(user.getEmail())) {
            return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorInfo("Ugyldig mail")).build();
        }

        if(!verifyPhoneNumber(user.getPhoneNumber())) {
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
}
